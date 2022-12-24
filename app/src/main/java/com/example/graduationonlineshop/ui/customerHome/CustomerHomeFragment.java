package com.example.graduationonlineshop.ui.customerHome;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.graduationonlineshop.Model.StoreOwner;
import com.example.graduationonlineshop.R;
import com.example.graduationonlineshop.StoreProductsActivity;
import com.example.graduationonlineshop.ViewHolder.StoreViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class CustomerHomeFragment extends Fragment {

     private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;

    DatabaseReference StoresOwnersRef;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_customer_home, container, false);

        database = FirebaseDatabase.getInstance();
        StoresOwnersRef = database.getReference("Stores Owners");


        recyclerView = root.findViewById(R.id.stores_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(layoutManager);

        return root;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<StoreOwner> options =
                new FirebaseRecyclerOptions.Builder<StoreOwner>()
                        .setQuery(StoresOwnersRef, StoreOwner.class)
                        .build();

        FirebaseRecyclerAdapter<StoreOwner, StoreViewHolder> adapter =
                new FirebaseRecyclerAdapter<StoreOwner, StoreViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@androidx.annotation.NonNull StoreViewHolder holder, int position, @androidx.annotation.NonNull final StoreOwner model) {
                        holder.txtStoreName.setText(model.getStoreName());
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), StoreProductsActivity.class);
                                intent.putExtra("storeId",model.getId());
                                startActivity(intent);

                            }
                        });


                    }


                    @androidx.annotation.NonNull
                    @Override
                    public StoreViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_layout, parent, false);
                        StoreViewHolder holder = new StoreViewHolder(view);
                        return holder;
                    }
                };


        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


}