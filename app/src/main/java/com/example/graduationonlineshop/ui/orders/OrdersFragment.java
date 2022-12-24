package com.example.graduationonlineshop.ui.orders;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationonlineshop.Admin.AdminUserProductsActivity;
import com.example.graduationonlineshop.Model.Order;
import com.example.graduationonlineshop.Prevalent.Prevalent;
import com.example.graduationonlineshop.R;
import com.example.graduationonlineshop.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.paperdb.Paper;


public class OrdersFragment extends Fragment {


    private RecyclerView ordersList;
    private DatabaseReference ordersRef;


    private OrdersViewModel ordersViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ordersViewModel =
                ViewModelProviders.of(this).get(OrdersViewModel.class);

        View root = inflater.inflate(R.layout.fragment_orders, container, false);


        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Paper.book().read(Prevalent.OwnerIdKey).toString());



        ordersList = root.findViewById(R.id.orders_list);
        ordersList.setLayoutManager(new LinearLayoutManager(getActivity()));

        return root;
    }


    @Override
    public void onStart() {
        super.onStart();



        FirebaseRecyclerOptions<Order> options=
                new FirebaseRecyclerOptions.Builder<Order>()
                        .setQuery(ordersRef,  Order.class)
                        .build();


        FirebaseRecyclerAdapter<Order, OrderViewHolder> adapter =
                new FirebaseRecyclerAdapter<Order,  OrderViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull OrderViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull final Order model)
                    {
                        holder.userName.setText(getString(R.string.order_name)+" "+ model.getCustomerName());
                        holder.userPhoneNumber.setText(getString(R.string.order_phone)+ " " +model.getCustomerPhone());
                        holder.userDateTime.setText(getString(R.string.order_at)+" "+ model.getDate()+"  "+ model.getTime());
                        holder.userShippingAddress.setText(getString(R.string.order_address)+" "+ model.getAddress());
                        holder.userTotalPrice.setText(model.getTotalPrice()+" $");

                        holder.showOrdersBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {

                                String uID = getRef(position).getKey();

                                Intent intent = new Intent(getActivity(), AdminUserProductsActivity.class);
                                intent.putExtra("customerId",model.getCustomerId());
                                intent.putExtra("storeId",model.getStoreId());


                                startActivity(intent);
                            }
                        });


                    }

                    @NonNull
                    @Override
                    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout, parent, false);
                        return new OrderViewHolder(view);
                    }
                };

        ordersList.setAdapter(adapter);
        adapter.startListening();

    }

}