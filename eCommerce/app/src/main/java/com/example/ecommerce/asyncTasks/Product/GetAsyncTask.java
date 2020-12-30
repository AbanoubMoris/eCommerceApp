package com.example.ecommerce.asyncTasks.Product;

import android.os.AsyncTask;


import com.example.ecommerce.Room.Products.ProductDao;
import com.example.ecommerce.models.ProductModel;

import java.util.List;

public class GetAsyncTask extends AsyncTask<Void,Void, List<ProductModel>> {

    ProductDao productDao;

    public GetAsyncTask(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    protected List<ProductModel> doInBackground(Void... voids) {
        return productDao.getAllProducts();
    }
}
