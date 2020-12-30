package com.example.ecommerce.models;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "products")
public class ProductModel implements Serializable {

    @NonNull
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "ProductName")
    private String ProductName;

    @ColumnInfo(name = "QR")
    private String QR;

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "product_description")
    private String product_description;

    @ColumnInfo(name = "product_pic")
    private String product_pic;

    @ColumnInfo(name = "product_price")
    private String product_price;

    @ColumnInfo(name = "product_quantity")
    private String product_quantity = "1";


    public ProductModel(String productName, String QR, String category, String product_description, String product_pic, String product_price, String product_quantity)
    {
        ProductName = productName;
        this.QR = QR;
        this.category = category;
        this.product_description = product_description;
        this.product_pic = product_pic;
        this.product_price = product_price;
        this.product_quantity = product_quantity;
    }

    public ProductModel() {
    }

    public ProductModel(ProductModel product) {
        ProductName = product.ProductName;
        this.QR = product.QR;
        this.category = product.category;
        this.product_description = product.product_description;
        this.product_pic = product.product_pic;
        this.product_price = product.product_price;
        this.product_quantity = product.product_quantity;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getQR() {
        return QR;
    }

    public void setQR(String QR) {
        this.QR = QR;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProduct_description() {
        return product_description;
    }

    public void setProduct_description(String product_description) {
        this.product_description = product_description;
    }

    public String getProduct_pic() {
        return product_pic;
    }

    public void setProduct_pic(String product_pic) {
        this.product_pic = product_pic;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public String getProduct_quantity() {
        return product_quantity;
    }

    public void setProduct_quantity(String product_quantity) {
        this.product_quantity = product_quantity;
    }
}
