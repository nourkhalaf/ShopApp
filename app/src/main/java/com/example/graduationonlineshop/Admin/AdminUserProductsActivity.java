package com.example.graduationonlineshop.Admin;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationonlineshop.Model.CartItem;
import com.example.graduationonlineshop.R;
import com.example.graduationonlineshop.ViewHolder.OrderProuctViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class AdminUserProductsActivity extends AppCompatActivity {

    private RecyclerView productsList;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference itemsListRef, orderRef;

    private Button confirmBtn;

    private String customerID="", storeID="";
    private ProgressDialog loadingBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_products);

        loadingBar = new ProgressDialog(AdminUserProductsActivity.this);


        customerID = getIntent().getStringExtra("customerId");
        storeID = getIntent().getStringExtra("storeId");

        productsList = findViewById(R.id.order_products_list);
        productsList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        productsList.setLayoutManager(layoutManager);


        confirmBtn = findViewById(R.id.order_items_confirm_btn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmOrder();
            }
        });

        itemsListRef = FirebaseDatabase.getInstance().getReference()
                .child("Cart List")
                .child(customerID)
                .child("Store Owner View")
                .child(storeID)
                .child("Products");

        orderRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(storeID)
                .child(customerID);

    }


    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<CartItem> options =
                new FirebaseRecyclerOptions.Builder<CartItem>()
                        .setQuery(itemsListRef,CartItem.class)
                        .build();

        FirebaseRecyclerAdapter<CartItem, OrderProuctViewHolder> adapter =
                new FirebaseRecyclerAdapter<CartItem, OrderProuctViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final OrderProuctViewHolder holder, int i, @NonNull final CartItem model)
                    {
                        holder.txtProductPrice.setText(model.getProductPrice()+" $");
                        holder.txtProductName.setText(model.getProductName());
                        holder.txtProductQuantity.setText(" quantity : "+model.getProductQuantity());
                        holder.txtProductNote.setText(model.getNote());

                        Picasso.get().load(model.getProductImage()).into(holder.productImage);

                    }

                    @NonNull
                    @Override
                    public OrderProuctViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_items_layout,parent,false);
                        OrderProuctViewHolder holder = new OrderProuctViewHolder(view);
                        return holder;

                    }
                };

        productsList.setAdapter(adapter);
        adapter.startListening();


    }

    private void confirmOrder(){

        loadingBar.setTitle("Confirm Order");
        loadingBar.setMessage("please wait ...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        itemsListRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            orderRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    loadingBar.dismiss();
                                    Toast.makeText(AdminUserProductsActivity.this, "Order Confirmed Successfully!" , Toast.LENGTH_LONG).show();

                                }

                            });


                        }

                    });

    }

}