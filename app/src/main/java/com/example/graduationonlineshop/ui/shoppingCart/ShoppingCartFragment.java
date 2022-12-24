package com.example.graduationonlineshop.ui.shoppingCart;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.graduationonlineshop.Model.CartItem;
import com.example.graduationonlineshop.Prevalent.Prevalent;
import com.example.graduationonlineshop.R;
import com.example.graduationonlineshop.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import io.paperdb.Paper;

public class ShoppingCartFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button nextProcessBtn, confirmBtn, cancelBtn;
    private TextView confirmTotalPrice;
    private EditText address;
    private String storeId="";

    private AlertDialog.Builder builder;
    private  AlertDialog dialog;
    private View orderSuccess;


    private ProgressDialog loadingBar;

    private int overTotalPrice = 0;

    private DatabaseReference cartListRef, orderRef;

    private ShoppingCartViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(ShoppingCartViewModel.class);
        View root = inflater.inflate(R.layout.fragment_shopping_cart, container, false);

        Paper.init(getContext());

        loadingBar = new ProgressDialog(getContext());

        cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders");


         builder = new AlertDialog.Builder(getContext());
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.confirm_order_layout,null);
        orderSuccess = LayoutInflater.from(getContext()).inflate(R.layout.success_confirm_order_layout,null);

        address = (EditText)itemView.findViewById(R.id.confirm_order_address);
        confirmTotalPrice = (TextView) itemView.findViewById(R.id.confirm_order_total_price);

        confirmBtn = (Button)itemView.findViewById(R.id.confirm_order_btn);
        cancelBtn = (Button)itemView.findViewById(R.id.cancel_confirm_order_btn);

        builder.setView(itemView);
        dialog = builder.create();

        recyclerView = root.findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        nextProcessBtn = (Button) root.findViewById(R.id.next_process_btn);

        // cartTotalAmount = (TextView) root.findViewById(R.id.shopping_cart_total_price);

        nextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (overTotalPrice==0)
                {
                    Toast.makeText(getContext(),"Your cart is empty!!",Toast.LENGTH_SHORT).show();
                }
                else {
                     showConfirmDialog();

                }
            }
        });

        return root;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<CartItem> options =
                new FirebaseRecyclerOptions.Builder<CartItem>()
                        .setQuery(cartListRef.child(Paper.book().read(Prevalent.CustomerIdKey).toString())
                                .child("User View")
                                .child("Products"),CartItem.class)
                        .build();

        FirebaseRecyclerAdapter<CartItem, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<CartItem, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final CartViewHolder holder, int i, @NonNull final CartItem model)
                    {
                        holder.txtProductPrice.setText(model.getProductPrice());
                        holder.txtProductName.setText(model.getProductName());
                        holder.txtProductQuantity.setNumber(model.getProductQuantity());
                        storeId = model.getStoreId();

                        Picasso.get().load(model.getProductImage()).into(holder.productImage);

                        int oneTypeProductTPrice = ((Integer.valueOf(model.getProductPrice()))) * Integer.valueOf(model.getProductQuantity());
                        overTotalPrice = overTotalPrice + oneTypeProductTPrice;

                        //confirmTotalPrice.setText(String.valueOf(overTotalPrice)+" $");
                        //cartTotalAmount.setText(String.valueOf(overTotalPrice)+" $");


                        holder.deleteProduct.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]
                                        {
                                                "Remove",
                                                "Cancel"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Remove Item?");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i)
                                    {
                                        if(i == 0)
                                        {
                                            int oneTypeProductTPrice = ((Integer.valueOf(model.getProductPrice()))) * Integer.valueOf(model.getProductQuantity());
                                            overTotalPrice = overTotalPrice - oneTypeProductTPrice;


                                            cartListRef.child(Paper.book().read(Prevalent.CustomerIdKey).toString())
                                                    .child("User View")
                                                    .child("Products")
                                                    .child(model.getProductId())
                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task)
                                                        {
                                                            if(task.isSuccessful())
                                                            {
                                                            }

                                                        }
                                                    });




                                            cartListRef.child(Paper.book().read(Prevalent.CustomerIdKey).toString())
                                                    .child("Store Owner View")
                                                    .child(storeId)
                                                    .child("Products")
                                                    .child(model.getProductId())
                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task)
                                                        {
                                                            if(task.isSuccessful())
                                                            {

                                                                Toast.makeText(getContext()," Item removed successfully", Toast.LENGTH_SHORT).show();

                                                            }

                                                        }
                                                    });


                                        }
                                        if(i == 1)
                                        {}

                                    }
                                });

                                builder.show();

                            }
                        });


                        holder.txtProductQuantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
                            @Override
                            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {

                                final HashMap<String, Object> itemMap = new HashMap<>();
                                itemMap.put("productQuantity",holder.txtProductQuantity.getNumber());

                                if (model.getProductQuantity().equals("1")&& holder.txtProductQuantity.getNumber().equals("1"))
                                {}
                                int oneTypeProductTPrice = ((Integer.valueOf(model.getProductPrice()))) * Integer.valueOf(model.getProductQuantity());
                                overTotalPrice = overTotalPrice  - oneTypeProductTPrice;


                                cartListRef.child(Paper.book().read(Prevalent.CustomerIdKey).toString())
                                        .child("User View")
                                        .child("Products").child(model.getProductId()).updateChildren(itemMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {

                                            }
                                            else
                                            {

                                            }
                                        }
                                    });

                                cartListRef.child(Paper.book().read(Prevalent.CustomerIdKey).toString())
                                        .child("Store Owner View")
                                        .child(storeId)
                                        .child("Products").child(model.getProductId()).updateChildren(itemMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                if (task.isSuccessful())
                                                {

                                                }
                                                else
                                                {

                                                }
                                            }
                                        });



                            }
                        });


                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                        CartViewHolder holder = new CartViewHolder(view);
                        return holder;

                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }



    public void showConfirmDialog(){

        confirmTotalPrice.setText(String.valueOf(overTotalPrice)+"$");
        dialog.show();


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(address.getText().toString()))
                {
                    Toast.makeText(getContext(),"Please enter your address",Toast.LENGTH_SHORT).show();

                }
                else
               {
                   confirmOrder();
               }
            }
        });
    }

    private void confirmOrder()
    {
        dialog.dismiss();
        loadingBar.setTitle("Confirm Order");
        loadingBar.setMessage("Please wait ...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();



        final String saveCurrentTime, saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());


        String name = Paper.book().read(Prevalent.CustomerNameKey).toString();
        String phone = Paper.book().read(Prevalent.CustomerPhoneKey).toString();
        final String id = Paper.book().read(Prevalent.CustomerIdKey).toString();

        final HashMap<String,Object> ordersMap = new HashMap<>();
        ordersMap.put("totalPrice", String.valueOf(overTotalPrice));
        ordersMap.put("customerName", name);
        ordersMap.put("customerPhone", phone);
        ordersMap.put("customerId", id);
        ordersMap.put("storeId", storeId);
        ordersMap.put("address",address.getText().toString());
        ordersMap.put("date",saveCurrentDate);
        ordersMap.put("time",saveCurrentTime);



        orderRef.child(storeId).child((Paper.book().read(Prevalent.CustomerIdKey).toString())).updateChildren(ordersMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            cartListRef.child(Paper.book().read(Prevalent.CustomerIdKey).toString()).child("User View").removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                loadingBar.dismiss();
                                                builder.setView(orderSuccess);
                                                dialog = builder.create();
                                                confirmTotalPrice.setText( "0 $");
                                                //cartTotalAmount.setText("0 $");

                                                dialog.show();

                                            }

                                        }
                                    });

                        }
                    }
                });

    }

}