package com.example.ecommerce.asyncTasks.CustomerProducts;

import android.os.AsyncTask;

import com.example.ecommerce.Room.Customer.CustomerDao;
import com.example.ecommerce.Room.Products.ProductDao;

public class DeleteAsyncTask extends AsyncTask<Void , Void , Void> {

    CustomerDao productDao;

    public DeleteAsyncTask(CustomerDao productDao) {
        this.productDao = productDao;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        productDao.deleteAllProducts();
        return null;
    }
}
