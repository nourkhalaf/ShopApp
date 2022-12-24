package com.example.graduationonlineshop;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


import com.example.graduationonlineshop.Buyers.LoginActivity;
import com.example.graduationonlineshop.Model.Customer;
import com.example.graduationonlineshop.Model.StoreOwner;
import com.example.graduationonlineshop.Prevalent.Prevalent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import io.paperdb.Paper;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private Button customerLoginButton, ownerLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Paper.init(this);

        ownerLoginButton = (Button) findViewById(R.id.owner_login_btn);
        customerLoginButton = (Button) findViewById(R.id.customer_login_btn);

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!=null)
        {

            String OwnerPasswordKey = Paper.book().read(Prevalent.OwnerPasswordKey);
            String OwnerNameKey = Paper.book().read(Prevalent.OwnerNameKey);

            String CustomerPasswordKey = Paper.book().read(Prevalent.CustomerPasswordKey);
            String CustomerNameKey = Paper.book().read(Prevalent.CustomerNameKey);

            if (OwnerNameKey != "" && OwnerPasswordKey != "")
            {
                if (!TextUtils.isEmpty(OwnerNameKey)  &&  !TextUtils.isEmpty(OwnerPasswordKey))
                {
                    StoreOwner model = new StoreOwner();
                    model.setName(OwnerNameKey);
                    model.setPhone(OwnerPasswordKey);
                    model.setId(user.getUid());

                    Intent intent = new Intent(MainActivity.this, OwnerHomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            else if (CustomerNameKey != "" && CustomerPasswordKey != "")
            {
                if (!TextUtils.isEmpty(CustomerNameKey)  &&  !TextUtils.isEmpty(CustomerPasswordKey))
                {

                    Intent intent = new Intent(MainActivity.this, CustomerHomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
            else
            {
                customerLoginButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, CustomerLoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

                ownerLoginButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }

        }

        else if(Paper.book().read(Prevalent.OwnerIdKey)!= null && Paper.book().read(Prevalent.OwnerNameKey)!= null &&
        Paper.book().read(Prevalent.OwnerPasswordKey) != null)
        {
            Intent intent = new Intent(MainActivity.this, OwnerHomeActivity.class);
            startActivity(intent);
            finish();
        }
        else if(Paper.book().read(Prevalent.CustomerIdKey)!= null && Paper.book().read(Prevalent.CustomerNameKey)!= null &&
                Paper.book().read(Prevalent.CustomerPasswordKey) != null)
        {
            Intent intent = new Intent(MainActivity.this, CustomerHomeActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            customerLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, CustomerLoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            ownerLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

        }

    }

}