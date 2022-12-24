package com.example.graduationonlineshop.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationonlineshop.Interface.ItemClickListener;
import com.example.graduationonlineshop.R;

public class StoreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    public TextView txtStoreName;
    public ImageView imageView;
    public ItemClickListener listener;


    public StoreViewHolder(View itemView)
    {
        super(itemView);


        imageView = (ImageView) itemView.findViewById(R.id.store_image);
        txtStoreName = (TextView) itemView.findViewById(R.id.store_name);

    }

    public void setItemClickListener(ItemClickListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void onClick(View view)
    {
        listener.onClick(view, getAdapterPosition(), false);
    }
}
