package com.example.graduationonlineshop.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.graduationonlineshop.Interface.ItemClickListener;
import com.example.graduationonlineshop.R;

public class OrderProuctViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName, txtProductPrice, txtProductQuantity, txtProductNote;
    private ItemClickListener itemClickListener;
    public ImageView productImage, deleteProduct;

    public OrderProuctViewHolder(@NonNull View itemView) {
        super(itemView);

        txtProductName = itemView.findViewById(R.id.order_product_name);
        txtProductPrice = itemView.findViewById(R.id.order_product_price);
        txtProductQuantity = itemView.findViewById(R.id.order_product_quantity);
        productImage = itemView.findViewById(R.id.order_product_image);
        txtProductNote = itemView.findViewById(R.id.order_product_note);

    }


    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
