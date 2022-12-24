package com.example.graduationonlineshop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationonlineshop.Model.Customer;
import com.example.graduationonlineshop.Prevalent.Prevalent;
import com.example.graduationonlineshop.R;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

import io.paperdb.Paper;

public class CustomerLoginActivity extends AppCompatActivity {

    private EditText loginInputPhone, loginInputPassword;
    private Button loginBtn, signBtn;
    private ProgressDialog loadingBar;

    private TextView customerSignUpTxt,customerLoginTxt,  customerPhone;
    private LinearLayout signUpLayout, loginMainLayout;
    private EditText customerName, customerPassword, customerRePassword;


    private final static int CUSTOMER_LOGIN_REQUEST_CODE = 7272;

    private List<AuthUI.IdpConfig> providers;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;

    FirebaseDatabase database;
    DatabaseReference CustomersRef;


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(CustomerLoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);

        Paper.init(this);

        database = FirebaseDatabase.getInstance();
        CustomersRef = database.getReference("Customers");

        providers = Arrays.asList(
                new AuthUI.IdpConfig.PhoneBuilder().build());

        firebaseAuth = FirebaseAuth.getInstance();


        customerName = (EditText) findViewById(R.id.customer_sign_up_name_input);
        customerPhone = (TextView) findViewById(R.id.customer_sign_up_phone_input);
        customerPassword = (EditText) findViewById(R.id.customer_sign_up_password_input);
        customerRePassword = (EditText) findViewById(R.id.customer_sign_up_re_password_input);

        signUpLayout = (LinearLayout) findViewById(R.id.customer_sign_up_layout);
        loginMainLayout = (LinearLayout) findViewById(R.id.customer_login_main_layout);

        loginBtn = (Button) findViewById(R.id.customer_login_btn);
        signBtn = (Button) findViewById(R.id.customer_sign_up_btn);
        loginInputPassword = (EditText) findViewById(R.id.customer_login_input_password);
        loginInputPhone = (EditText) findViewById(R.id.customer_login_input_phone);

        customerSignUpTxt = (TextView) findViewById(R.id.customer_sign_up_txt);
        customerLoginTxt = (TextView) findViewById(R.id.customer_login_txt);

        customerLoginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Paper.book().destroy();
                loginMainLayout.setVisibility(View.VISIBLE);
                signUpLayout.setVisibility(View.GONE);

            }
        });

        customerSignUpTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthMethodPickerLayout authMethodPickerLayout = new AuthMethodPickerLayout
                        .Builder(R.layout.activity_main)
                        .setPhoneButtonId(R.id.customer_login_btn)
                        .build();

                startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAuthMethodPickerLayout(authMethodPickerLayout)
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(providers)
                        .build(), CUSTOMER_LOGIN_REQUEST_CODE);
    }
});


        loadingBar = new ProgressDialog(this);





        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                loginUser();
            }
        });




    }

    private void loginUser()
    {
        String phone = loginInputPhone.getText().toString();
        String password = loginInputPassword.getText().toString();

        if (TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "Please write your phone number...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please write your password...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();


            AllowAccessToAccount(phone, password);
        }
    }

    private void AllowAccessToAccount(final String phone, final String password)
    {
        CustomersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(phone).exists())

                {
                    Customer customerData = dataSnapshot.child(phone).getValue(Customer.class);

                    if(customerData != null){
                        if (customerData.getPhone().equals(phone))
                        {
                            if (customerData.getPassword().equals(password))
                            {
                                Toast.makeText(CustomerLoginActivity.this, "Welcome Admin, you are logged in Successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                final Customer customer = new Customer();
                                customer.setName(customerData.getName());
                                customer.setPhone(customerData.getPhone());
                                customer.setId(customerData.getId());

                                 Paper.book().write(Prevalent.CustomerIdKey, customerData.getId());
                                Paper.book().write(Prevalent.CustomerNameKey, customerData.getName());
                                Paper.book().write(Prevalent.CustomerPhoneKey, customerData.getPhone());
                                Paper.book().write(Prevalent.CustomerPasswordKey, customerData.getPassword());


                                Intent intent = new Intent(CustomerLoginActivity.this, CustomerHomeActivity.class);
                                startActivity(intent);

                            }
                            else
                            {
                                loadingBar.dismiss();
                                Toast.makeText(CustomerLoginActivity.this, "Password is incorrect.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                else
                {
                    Toast.makeText(CustomerLoginActivity.this, "Account with this " + phone + " number do not exists.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CUSTOMER_LOGIN_REQUEST_CODE)
        {
            if (data!=null) {
                IdpResponse response = IdpResponse.fromResultIntent(data);
                if (resultCode == RESULT_OK) {
                    loadingBar.setTitle("Verification");
                    loadingBar.setMessage("Please wait, while we are checking the credentials.");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        customerPhone.setText(user.getPhoneNumber());
                        checkRegistration(user.getPhoneNumber());

                    }

                } else {
                    loadingBar.dismiss();
                    Toast.makeText(CustomerLoginActivity.this, "[ERROR:]" + response.getError().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
//                Intent intent = new Intent(this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);

            }
        }

    }

    private void checkRegistration(String phone) {

        CustomersRef.child(phone).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                    Customer customerData = snapshot.getValue(Customer.class);

                     Paper.book().write(Prevalent.CustomerIdKey, customerData.getId());
                    Paper.book().write(Prevalent.CustomerNameKey, customerData.getName());
                    Paper.book().write(Prevalent.CustomerPhoneKey, customerData.getPhone());

                    Paper.book().write(Prevalent.CustomerPasswordKey, customerData.getPassword());


                    Toast.makeText(CustomerLoginActivity.this, "Account with this phone  number exists, login ", Toast.LENGTH_SHORT).show();

                    loadingBar.dismiss();

                }

                else {
                    loadingBar.dismiss();
                    showOwnerSignUpLayout();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showOwnerSignUpLayout() {

        loginMainLayout.setVisibility(View.GONE);
        signUpLayout.setVisibility(View.VISIBLE);

        signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(customerName.getText().toString()))
                {
                    Toast.makeText(CustomerLoginActivity.this," Enter your name",Toast.LENGTH_SHORT).show();

                }
                else  if(TextUtils.isEmpty(customerPassword.getText().toString()))
                {
                    Toast.makeText(CustomerLoginActivity.this,"Enter password",Toast.LENGTH_SHORT).show();
                }
                else  if(TextUtils.isEmpty(customerRePassword.getText().toString()))
                {
                    Toast.makeText(CustomerLoginActivity.this,"Enter re-password",Toast.LENGTH_SHORT).show();
                }
                else  if(!customerRePassword.getText().toString().equals(customerPassword.getText().toString()))
                {
                    Toast.makeText(CustomerLoginActivity.this,"Passwords do not match",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    final Customer model = new Customer();
                    model.setName(customerName.getText().toString());
                    model.setPassword(customerPassword.getText().toString());
                    model.setPhone(customerPhone.getText().toString());
                    model.setId(FirebaseAuth.getInstance().getCurrentUser().getUid());

                     Paper.book().write(Prevalent.CustomerIdKey, FirebaseAuth.getInstance().getCurrentUser().getUid());
                    Paper.book().write(Prevalent.CustomerNameKey, model.getName());
                    Paper.book().write(Prevalent.CustomerPhoneKey,  model.getPhone());
                    Paper.book().write(Prevalent.CustomerPasswordKey, model.getPassword());


                    CustomersRef.child(customerPhone.getText().toString())
                            .setValue(model)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(CustomerLoginActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(CustomerLoginActivity.this,"register_successfully", Toast.LENGTH_SHORT)
                                    .show();

                            showHomeFragment();

                        }
                    });


                }
            }
        });

    }

    public void showHomeFragment(){
        Intent intent = new Intent(CustomerLoginActivity.this, CustomerHomeActivity.class);
        startActivity(intent);

    }

}
