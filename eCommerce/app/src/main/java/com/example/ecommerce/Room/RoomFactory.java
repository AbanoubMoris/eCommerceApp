package com.example.ecommerce.Room;

import android.content.Context;

import androidx.room.Room;

import com.example.ecommerce.Room.Customer.CustomerDb;
import com.example.ecommerce.Room.Products.ProductsDb;

public class RoomFactory {

    private static ProductsDb productsDb;
    private static CustomerDb CustomerDb;

    public static ProductsDb getProductsDb(Context context) {

        if (productsDb == null) {
            productsDb = Room.databaseBuilder(context, ProductsDb.class, "products_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return productsDb;

    }
    public static CustomerDb getCustomerDao(Context context) {

        if (productsDb == null) {
            CustomerDb = Room.databaseBuilder(context, CustomerDb.class, "products_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return CustomerDb;

    }
}
