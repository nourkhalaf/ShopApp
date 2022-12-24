package com.example.graduationonlineshop.Buyers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.graduationonlineshop.Admin.AdminUserProductsActivity;
import com.example.graduationonlineshop.Model.Product;
import com.example.graduationonlineshop.Prevalent.Prevalent;
import com.example.graduationonlineshop.R;
import com.example.graduationonlineshop.StoreProductsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import io.paperdb.Paper;

public class ProductDetailsActivity extends AppCompatActivity {

    private Button addToCartBtn;
    private ImageView productImage;
    private ElegantNumberButton numberButton;
    private TextView productName,productDescription,productPrice;
    private EditText productNotes;
    private String productID = "", storeID = "", state = "";

    private Product productData;

    private ProgressDialog loadingBar;

    private DatabaseReference cartListRef, productsRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Paper.init(this);

        loadingBar = new ProgressDialog(ProductDetailsActivity.this);


        productID = getIntent().getStringExtra("productId");
        storeID = getIntent().getStringExtra("storeId");


        cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products").child(storeID).child(productID);


        addToCartBtn = (Button) findViewById(R.id.add_product_to_cart_btn);
        numberButton = (ElegantNumberButton) findViewById(R.id.number_btn);
        productImage = (ImageView) findViewById(R.id.product_image_details);
        productName = (TextView) findViewById(R.id.product_name_details);
        productPrice = (TextView) findViewById(R.id.product_price_details);
        productDescription = (TextView) findViewById(R.id.product_description_details);
        productNotes = (EditText) findViewById(R.id.product_notes_details);

        getProductDetails(productID);

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOrderState();

            }
        });



    }


    private void addingToCartList()
    {

        loadingBar.setTitle("Adding to cart");
        loadingBar.setMessage("please wait ...");
        loadingBar.setCanceledOnTouchOutside(false);

        if (!ProductDetailsActivity.this.isFinishing()) {
            loadingBar.show();
        }




            String saveCurrentTime, saveCurrentDate;

            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
            saveCurrentDate = currentDate.format(calForDate.getTime());

            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
            saveCurrentTime = currentTime.format(calForDate.getTime());


            final HashMap<String, Object> cartMap = new HashMap<>();
            cartMap.put("productId", productID);
            cartMap.put("productName", productName.getText().toString());
            cartMap.put("productPrice", productPrice.getText().toString());
            cartMap.put("productNotes", productNotes.getText().toString());
            cartMap.put("productQuantity", numberButton.getNumber());
            cartMap.put("productImage", productData.getImage());
            cartMap.put("date", saveCurrentDate);
            cartMap.put("storeId", storeID);




            cartListRef.child(Paper.book().read(Prevalent.CustomerIdKey).toString())
                    .child("User View")
                    .child("Products")
                    .child(productID)
                    .updateChildren(cartMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                            }
                        }
                    });




        cartListRef.child(Paper.book().read(Prevalent.CustomerIdKey).toString())
                .child("Store Owner View")
                .child(storeID)
                .child("Products")
                .child(productID)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            loadingBar.dismiss();
                            Toast.makeText(ProductDetailsActivity.this, "Added to cart list.", Toast.LENGTH_SHORT).show();

                        }
                    }
                });


    }

    private void getProductDetails(String productID)
    {


        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                    Product product = snapshot.getValue(Product.class);

                    productData = product;
                    productName.setText(product.getName());
                    productDescription.setText(product.getDescription());
                    productPrice.setText(product.getPrice());

                    Picasso.get().load(product.getImage()).into(productImage);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkOrderState()
    {
        cartListRef.child(Paper.book().read(Prevalent.CustomerIdKey).toString()).child("User View").child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists()) {
                    cartListRef.child(Paper.book().read(Prevalent.CustomerIdKey).toString()).child("User View").child("Products").orderByChild("storeId").equalTo(storeID)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists())
                                addingToCartList();
                            else
                                Toast.makeText(ProductDetailsActivity.this,"You can purchase from this store after complete your current order or empty your shopping cart. ",Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                             }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });


        cartListRef.child(Paper.book().read(Prevalent.CustomerIdKey).toString()).child("User View").child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(!snapshot.exists()) {
                    addingToCartList();
                 }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }
}