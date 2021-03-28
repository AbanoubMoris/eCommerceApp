package com.example.ecommerce.adapters;

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

public class CartRvAdapter extends RecyclerView.Adapter<CartRvAdapter.CartViewHolder> {

    List<ProductModel> productsList;
    Context context;
    OnIncClick onIncClick;
    OnDecClick onDecClick;
    OnItemClick onItemClick;

    public interface OnItemClick {
        void onProductClick(View view, int position);
    }

    public interface OnIncClick {
        void onInc(View view, int position);
    }

    public interface OnDecClick {
        void onDec(View view, int position);
    }


    public CartRvAdapter(List<ProductModel> productsList, Context context, OnIncClick onIncClick, OnDecClick onDecClick, OnItemClick onItemClick) {
        this.productsList = productsList;
        this.context = context;
        this.onIncClick = onIncClick;
        this.onDecClick = onDecClick;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_rv_item, parent, false);

        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartViewHolder holder, int position) {

        ProductModel model = productsList.get(position);
        Glide.with(context).load(model.getProduct_pic()).into(holder.image);
        holder.titleTv.setText(model.getProductName());
        holder.detailsTv.setText(model.getProduct_description());
        holder.priceTv.setText(model.getProduct_price());
        holder.quantityTv.setText(model.getProduct_quantity() + "");

        holder.incIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onIncClick.onInc(view, holder.getAdapterPosition());
            }
        });

        holder.decIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDecClick.onDec(view, holder.getAdapterPosition());

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick.onProductClick(view, holder.getAdapterPosition());
            }
        });


    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView titleTv;
        TextView detailsTv;
        public TextView priceTv;
        TextView quantityTv;
        ImageButton incIb;
        ImageButton decIb;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.product_cart_iv);
            titleTv = itemView.findViewById(R.id.title_cart_tv);
            detailsTv = itemView.findViewById(R.id.details_cart_tv);
            priceTv = itemView.findViewById(R.id.price_cart_tv);
            quantityTv = itemView.findViewById(R.id.quantity_tv);
            incIb = itemView.findViewById(R.id.inc_ib);
            decIb = itemView.findViewById(R.id.dec_ib);
        }
    }


}
