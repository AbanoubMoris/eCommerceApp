package com.example.ecommerce.Room.Products;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ecommerce.models.ProductModel;
import java.util.List;

@Dao
public interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertProduct(ProductModel productModel);

    @Query("SELECT * FROM products")
    List<ProductModel> getAllProducts();

    @Query("DELETE FROM products")
    void deleteAllProducts();

    @Delete
    void deleteProduct(ProductModel productModel);

    @Update
    void updateProduct(ProductModel productModel);

}
