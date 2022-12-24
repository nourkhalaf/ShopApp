package com.example.graduationonlineshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.graduationonlineshop.Buyers.ProductDetailsActivity;
import com.example.graduationonlineshop.Model.Product;
import com.example.graduationonlineshop.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class StoreProductsActivity extends AppCompatActivity {

    private DatabaseReference ProductsRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    String storeId;

    private TextView searchBtn;
    private EditText inputText;
    private String searchInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_products);

         storeId = getIntent().getStringExtra("storeId");


        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products").child(storeId);


        recyclerView = findViewById(R.id.store_products_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(StoreProductsActivity.this,2);
        recyclerView.setLayoutManager(layoutManager);

        inputText = (EditText) findViewById(R.id.search_product_name);
        searchBtn = (TextView) findViewById(R.id.search_btn);



        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                searchInput = inputText.getText().toString();

                onStart();
            }
        });
     }

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(ProductsRef.orderByChild("name").startAt(searchInput), Product.class)
                        .build();

        FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@androidx.annotation.NonNull ProductViewHolder holder, int position, @androidx.annotation.NonNull final Product model) {
                        holder.txtProductName.setText(model.getName());
                        holder.txtProductPrice.setText(model.getPrice());
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(StoreProductsActivity.this, ProductDetailsActivity.class);
                                intent.putExtra("productId",model.getProductId());
                                intent.putExtra("storeId",storeId);
                                startActivity(intent);

                            }
                        });


                    }


                    @androidx.annotation.NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };


        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

}