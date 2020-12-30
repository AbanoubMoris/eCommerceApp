package com.example.ecommerce.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerce.R;
import com.example.ecommerce.models.ProductModel;


import java.util.List;


public class ProductRvAdapter extends RecyclerView.Adapter<ProductRvAdapter.ProductsViewHolder> {


    List<ProductModel> productModelList;

    OnProductClick onProductClick;
    OnProductClick onAddClick;

    Context context;

    public ProductRvAdapter(List<ProductModel> noteList,Context context, OnProductClick onProductClick,OnProductClick onAddClick) {
        this.productModelList = noteList;
        this.context= context;
        this.onProductClick = onProductClick;
        this.onAddClick = onAddClick;
    }

    public interface OnProductClick {
        void onClick(View view, int position);
    }


    @NonNull
    @Override
    public ProductsViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        //inflate/inflater -> means connect XML with java
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_rv_item, parent, false);

        return new ProductsViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ProductsViewHolder holder, final int position) {
        ProductModel product = productModelList.get(position);


        holder.nameTv.setText(product.getProductName());
        holder.details_tv.setText(product.getProduct_description());
        holder.price_tv.setText(String.valueOf(product.getProduct_price()));

        Glide.with(context).load(product.getProduct_pic()).into(holder.product_iv);

        holder.product_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProductClick.onClick(v, position);
            }
        });
        holder.add_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddClick.onClick(v,position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return productModelList.size();
    }

    class ProductsViewHolder extends RecyclerView.ViewHolder {

        ImageView product_iv;
        TextView nameTv;
        TextView details_tv;
        TextView price_tv;
        //TextView Quantity_tv;
        ImageButton add_ib;
        View itemV;


        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);

            this.itemV = itemView;
            nameTv = itemView.findViewById(R.id.title_tv);
           // Quantity_tv = itemView.findViewById(R.id.quantity_tv);
            details_tv = itemView.findViewById(R.id.details_tv);
            product_iv = itemView.findViewById(R.id.product_iv);
            price_tv = itemView.findViewById(R.id.price_tv);
            add_ib = itemView.findViewById(R.id.add_ib);

        }
    }
}
