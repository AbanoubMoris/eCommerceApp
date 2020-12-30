package com.example.ecommerce.asyncTasks.Product;

import android.os.AsyncTask;

import com.example.ecommerce.Room.Products.ProductDao;

public class DeleteAsyncTask extends AsyncTask<Void , Void , Void> {

    ProductDao productDao;

    public DeleteAsyncTask(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        productDao.deleteAllProducts();
        return null;
    }
}
