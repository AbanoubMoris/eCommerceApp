package com.example.ecommerce.models;

import androidx.annotation.NonNull;
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

    @ColumnInfo(name = "product_price")
    private double product_price ;

    public CustomerProducts() {
    }

    public CustomerProducts(@NonNull String productName, int product_quantity, double product_price) {
        ProductName = productName;
        this.product_quantity = product_quantity;
        this.product_price = product_price;
    }

    public double getProduct_price() {
        return product_price;
    }

    public void setProduct_price(double product_price) {
        this.product_price = product_price;
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
