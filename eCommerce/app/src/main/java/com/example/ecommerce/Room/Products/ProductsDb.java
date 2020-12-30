package com.example.ecommerce.Room.Products;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.ecommerce.models.ProductModel;

@Database(entities = {ProductModel.class}, version = 1, exportSchema = false)
public abstract class ProductsDb extends RoomDatabase {

    public abstract ProductDao getProductDao();

}
