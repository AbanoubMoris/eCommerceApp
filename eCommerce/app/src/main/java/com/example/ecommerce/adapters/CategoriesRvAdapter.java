package com.example.ecommerce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.ecommerce.R;
import com.example.ecommerce.models.CategoriesModel;

import java.util.List;

public class CategoriesRvAdapter extends RecyclerView.Adapter<CategoriesRvAdapter.CategoriesViewHolder> {

    List<CategoriesModel> categoriesList;
    Context context;
    private OnCategoryClick onCategoryClick;

    public CategoriesRvAdapter(List<CategoriesModel> categoriesList, Context context,OnCategoryClick onCategoryClick) {
        this.categoriesList = categoriesList;
        this.context = context;
        this.onCategoryClick = onCategoryClick;
    }
    public interface OnCategoryClick {
        void onClick(View view, int position);
    }

    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_rv_item, parent, false);

        return new CategoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesViewHolder holder, final int position) {

        CategoriesModel model = categoriesList.get(position);
        Glide.with(context).load(model.getCategory_pic()).into(holder.image);
        holder.nameTv.setText(model.getCategory_Name());
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCategoryClick.onClick(v,position);
            }
        });
        holder.nameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCategoryClick.onClick(v,position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    class CategoriesViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView nameTv;

        public CategoriesViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.category_iv);
            nameTv = itemView.findViewById(R.id.category_tv);
        }
    }

}
