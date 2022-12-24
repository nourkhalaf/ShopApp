package com.example.graduationonlineshop.Buyers;

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

import com.example.graduationonlineshop.Model.StoreOwner;
import com.example.graduationonlineshop.OwnerHomeActivity;
import com.example.graduationonlineshop.Prevalent.Prevalent;
import com.example.graduationonlineshop.R;
import com.example.graduationonlineshop.MainActivity;
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


public class LoginActivity extends AppCompatActivity
{
    private EditText loginInputPhone, loginInputPassword;
    private Button loginBtn, signBtn;
    private ProgressDialog loadingBar;

    private TextView ownerSignUpTxt,ownerLoginTxt, ownerPhone;
    private LinearLayout signUpLayout, loginMainLayout;
    private EditText ownerName, storeName, ownerPassword, ownerRePassword, ownerEmail;


    private final static int OWNER_LOGIN_REQUEST_CODE = 7272;

    private List<AuthUI.IdpConfig> providers;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;

    FirebaseDatabase database;

    DatabaseReference StoresOwnersRef;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Paper.init(this);

        database = FirebaseDatabase.getInstance();
        StoresOwnersRef = database.getReference("Stores Owners");

         providers = Arrays.asList(
                new AuthUI.IdpConfig.PhoneBuilder().build());

        firebaseAuth = FirebaseAuth.getInstance();


        ownerName = (EditText) findViewById(R.id.sign_up_name_input);
        storeName = (EditText) findViewById(R.id.sign_up_store_name_input);
        ownerPhone= (TextView) findViewById(R.id.sign_up_phone_input);
        ownerEmail= (EditText) findViewById(R.id.sign_up_email_input);
        ownerPassword = (EditText) findViewById(R.id.sign_up_password_input);
        ownerRePassword = (EditText) findViewById(R.id.sign_up_re_password_input);

        signUpLayout = (LinearLayout) findViewById(R.id.sign_up_layout);
        loginMainLayout = (LinearLayout) findViewById(R.id.login_main_layout);

        loginBtn = (Button) findViewById(R.id.owner_login_btn);
        signBtn = (Button) findViewById(R.id.sign_up_btn);
        loginInputPassword = (EditText) findViewById(R.id.login_input_password);
        loginInputPhone = (EditText) findViewById(R.id.login_input_phone);

        ownerSignUpTxt = (TextView) findViewById(R.id.owner_sign_up_txt);
        ownerLoginTxt = (TextView) findViewById(R.id.owner_login_txt);

        ownerLoginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Paper.book().destroy();
                loginMainLayout.setVisibility(View.VISIBLE);
                signUpLayout.setVisibility(View.GONE);
            }
        });

        ownerSignUpTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthMethodPickerLayout authMethodPickerLayout = new AuthMethodPickerLayout
                        .Builder(R.layout.activity_main)
                        .setPhoneButtonId(R.id.owner_login_btn)
                        .build();

                startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAuthMethodPickerLayout(authMethodPickerLayout)
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(providers)
                        .build(), OWNER_LOGIN_REQUEST_CODE);
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
        StoresOwnersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(phone).exists())

                {
                    StoreOwner ownerData = dataSnapshot.child(phone).getValue(StoreOwner.class);

                    if(ownerData != null){
                    if (ownerData.getPhone().equals(phone))
                    {
                        if (ownerData.getPassword().equals(password))
                        {
                                Toast.makeText(LoginActivity.this, "Welcome Admin, you are logged in Successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                            final StoreOwner owner = new StoreOwner();
                            owner.setName(ownerData.getName());
                            owner.setEmail(ownerData.getEmail());
                            owner.setPhone(ownerData.getPhone());
                            owner.setStoreName(ownerData.getStoreName());
                            owner.setId(ownerData.getId());

                             Paper.book().write(Prevalent.OwnerIdKey, ownerData.getId());
                            Paper.book().write(Prevalent.OwnerNameKey, ownerData.getName());
                            Paper.book().write(Prevalent.OwnerPasswordKey, ownerData.getPassword());
                            Paper.book().write(Prevalent.OwnerPhoneKey, ownerData.getPhone());


                            Intent intent = new Intent(LoginActivity.this, OwnerHomeActivity.class);
                               startActivity(intent);

                        }
                        else
                        {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Password is incorrect.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    }
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Account with this " + phone + " number do not exists.", Toast.LENGTH_SHORT).show();
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
         if(requestCode == OWNER_LOGIN_REQUEST_CODE)
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
                        ownerPhone.setText(user.getPhoneNumber());
                        checkRegistration(user.getPhoneNumber());

                    }

                } else {
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this, "[ERROR:]" + response.getError().getMessage(), Toast.LENGTH_SHORT).show();
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

        StoresOwnersRef.child(phone).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                    StoreOwner owner = snapshot.getValue(StoreOwner.class);

                     Paper.book().write(Prevalent.OwnerNameKey, owner.getName());
                    Paper.book().write(Prevalent.OwnerPasswordKey, owner.getPassword());
                    Paper.book().write(Prevalent.OwnerPhoneKey, owner.getPhone());
                    Paper.book().write(Prevalent.OwnerIdKey, owner.getId());


                    Toast.makeText(LoginActivity.this, "Account with this phone  number exists, login ", Toast.LENGTH_SHORT).show();

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
                if(TextUtils.isEmpty(ownerName.getText().toString()))
                {
                    Toast.makeText(LoginActivity.this," Enter your name",Toast.LENGTH_SHORT).show();

                }else  if(TextUtils.isEmpty(storeName.getText().toString()))
                {
                    Toast.makeText(LoginActivity.this,"Enter store name",Toast.LENGTH_SHORT).show();
                 }
                else  if(TextUtils.isEmpty(ownerPassword.getText().toString()))
                {
                    Toast.makeText(LoginActivity.this,"Enter password",Toast.LENGTH_SHORT).show();
                 }
                else  if(TextUtils.isEmpty(ownerRePassword.getText().toString()))
                {
                    Toast.makeText(LoginActivity.this,"Enter re-password",Toast.LENGTH_SHORT).show();
                 }
                else  if(TextUtils.isEmpty(ownerEmail.getText().toString()))
                {
                    Toast.makeText(LoginActivity.this,"Enter email",Toast.LENGTH_SHORT).show();
                 }
                else  if(!ownerRePassword.getText().toString().equals(ownerPassword.getText().toString()))
                {
                    Toast.makeText(LoginActivity.this,"Passwords do not match",Toast.LENGTH_SHORT).show();
                 }
                else
                {
                    final StoreOwner model = new StoreOwner();
                    model.setName(ownerName.getText().toString());
                    model.setStoreName(storeName.getText().toString());
                    model.setPassword(ownerPassword.getText().toString());
                    model.setEmail(ownerEmail.getText().toString());
                    model.setPassword(ownerPassword.getText().toString());
                    model.setPhone(ownerPhone.getText().toString());
                    model.setId(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    Paper.book().write(Prevalent.OwnerIdKey, FirebaseAuth.getInstance().getCurrentUser().getUid());
                    Paper.book().write(Prevalent.OwnerNameKey, model.getName());
                    Paper.book().write(Prevalent.OwnerPhoneKey, model.getPhone());
                    Paper.book().write(Prevalent.OwnerPasswordKey, model.getPassword());


                    StoresOwnersRef.child(ownerPhone.getText().toString())
                            .setValue(model)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(LoginActivity.this,"register_successfully", Toast.LENGTH_SHORT)
                                    .show();


                            Intent intent = new Intent(LoginActivity.this, OwnerHomeActivity.class);
                            startActivity(intent);

                        }
                    });


                }
            }
        });



//        //set data
//        if(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() !=null &&
//                !TextUtils.isEmpty(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))
//            edt_phone.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
//

    }


}
