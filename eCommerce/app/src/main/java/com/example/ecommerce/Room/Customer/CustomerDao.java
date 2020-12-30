package com.example.ecommerce.Room.Customer;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ecommerce.models.CustomerProducts;
import com.example.ecommerce.models.ProductModel;

import java.util.List;

@Dao
public interface CustomerDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertProduct(CustomerProducts productModel);

    @Query("SELECT * FROM Customer_products")
    List<CustomerProducts> getAllProducts();

    @Query("DELETE FROM Customer_products")
    void deleteAllProducts();

    @Delete
    void deleteProduct(CustomerProducts customerProducts);

    @Update
    void updateProduct(CustomerProducts customerProducts);

}
