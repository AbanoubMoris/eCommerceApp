package com.example.ecommerce.Room.Customer;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.ecommerce.models.CustomerProducts;

@Database(entities = {CustomerProducts.class}, version = 2, exportSchema = false)
public abstract class CustomerDb extends RoomDatabase {

    public abstract CustomerDao getCustomerDao();

}
