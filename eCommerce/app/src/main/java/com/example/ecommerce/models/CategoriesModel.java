package com.example.ecommerce.models;


import java.io.Serializable;

public class CategoriesModel implements Serializable {

    private String category_pic;

    private String category_Name;

    public CategoriesModel() {
    }

    public CategoriesModel(String category_pic, String category_Name) {
        this.category_pic = category_pic;
        this.category_Name = category_Name;
    }

    public String getCategory_pic() {
        return category_pic;
    }

    public void setCategory_pic(String category_pic) {
        this.category_pic = category_pic;
    }

    public String getCategory_Name() {
        return category_Name;
    }

    public void setCategory_Name(String category_Name) {
        this.category_Name = category_Name;
    }
}
