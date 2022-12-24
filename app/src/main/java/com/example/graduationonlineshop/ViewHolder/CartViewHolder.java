package com.example.graduationonlineshop.ViewHolder;

import android.app.Notification;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.graduationonlineshop.Interface.ItemClickListener;
import com.example.graduationonlineshop.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName, txtProductPrice;
    private ItemClickListener itemClickListener;
    public ElegantNumberButton txtProductQuantity;
    public ImageView productImage, deleteProduct;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        txtProductName = itemView.findViewById(R.id.cart_product_name);
        txtProductPrice = itemView.findViewById(R.id.cart_product_price);
        txtProductQuantity = itemView.findViewById(R.id.cart_product_quantity_btn);
        productImage = itemView.findViewById(R.id.cart_product_image);
        deleteProduct = itemView.findViewById(R.id.cart_product_delete_btn);

    }


    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
