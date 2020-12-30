package com.example.ecommerce.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "Customer_products")
public class CustomerProducts implements Serializable {

    @NonNull()
    @PrimaryKey()
    @ColumnInfo(name = "ProductName")
    private String ProductName;

    @ColumnInfo(name = "product_quantity")
    private int product_quantity ;

    public CustomerProducts() {
    }

    public CustomerProducts(String productName, int product_quantity) {
        ProductName = productName;
        this.product_quantity = product_quantity;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public int getProduct_quantity() {
        return product_quantity;
    }

    public void setProduct_quantity(int product_quantity) {
        this.product_quantity = product_quantity;
    }
}
