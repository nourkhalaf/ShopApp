package com.example.graduationonlineshop;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationonlineshop.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import io.paperdb.Paper;

public class UpdateProductActivity extends AppCompatActivity {
    private String productId, Name, Description , Price;
    private Button saveChangesBtn, deleteProductBtn;
    private ImageView UpdateImage;
    private EditText UpdateProductName, UpdateProductDescription,UpdateProductPrice;

    private DatabaseReference ProductsRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);


        productId = getIntent().getStringExtra("productId");

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products").child(Paper.book().read(Prevalent.OwnerIdKey).toString()).child(productId);




        saveChangesBtn = (Button) findViewById(R.id.save_changes_btn);
        deleteProductBtn = (Button) findViewById(R.id.delete_btn);
        UpdateImage = (ImageView) findViewById(R.id.update_product_image);
        UpdateProductName = (EditText) findViewById(R.id.update_product_name);
        UpdateProductDescription = (EditText) findViewById(R.id.update_product_description);
        UpdateProductPrice = (EditText) findViewById(R.id.update_product_price);

        displaySpecificItemInfo();


        loadingBar = new ProgressDialog(this);





        saveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                ValidateProductData();
            }
        });

        deleteProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                deleteProduct();
            }
        });
    }






    private void ValidateProductData()
    {
        Name = UpdateProductName.getText().toString();
        Description = UpdateProductDescription.getText().toString();
        Price = UpdateProductPrice.getText().toString();


        if (TextUtils.isEmpty(Name))
        {
            Toast.makeText(this,getString(R.string.toast_add_product_name) , Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Description))
        {
            Toast.makeText(this,getString(R.string.toast_add_product_description) , Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Price))
        {
            Toast.makeText(this,getString(R.string.toast_add_product_price), Toast.LENGTH_SHORT).show();
        }
        else
        {
            StoreProductInformation();
        }
    }



    private void StoreProductInformation()
    {
        loadingBar.setTitle(getString(R.string.loading_update_product_title));
        loadingBar.setMessage(getString(R.string.loading_update_product_message) );
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        SaveProductInfoToDatabase();

    }

    private void SaveProductInfoToDatabase()
    {
        HashMap<String, Object> itemMap = new HashMap<>();
        itemMap.put("name", Name);
        itemMap.put("description", Description);
        itemMap.put("price", Price);


        ProductsRef.updateChildren(itemMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            loadingBar.dismiss();
                            Toast.makeText(UpdateProductActivity.this, getString(R.string.toast_update_product_successfully) , Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(UpdateProductActivity.this, getString(R.string.toast_add_product_fail) + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void displaySpecificItemInfo() {

        ProductsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                    String screenName = snapshot.child("name").getValue().toString();
                    String screenDescription = snapshot.child("description").getValue().toString();
                    String screenPrice = snapshot.child("price").getValue().toString();
                    String screenImage = snapshot.child("image").getValue().toString();


                    UpdateProductName.setText(screenName);

                    UpdateProductDescription.setText(screenDescription);
                    UpdateProductPrice.setText(screenPrice);


                    Picasso.get().load(screenImage).into(UpdateImage);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deleteProduct() {
        CharSequence options[] = new CharSequence[]
                {
                        getString(R.string.ok),
                        getString(R.string.cancel)
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProductActivity.this);
        builder.setTitle(getString(R.string.alert_delete_title));
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i)
            {
                if(i == 0)
                {
                    ProductsRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            Toast.makeText(UpdateProductActivity.this,getString(R.string.toast_delete), Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    });
                }
                if(i == 1)
                {

                }

            }
        });
        builder.show();

    }


}
