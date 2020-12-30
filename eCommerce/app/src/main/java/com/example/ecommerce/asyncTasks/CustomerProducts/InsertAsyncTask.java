package com.example.ecommerce.asyncTasks.CustomerProducts;

import android.os.AsyncTask;

import com.example.ecommerce.Room.Customer.CustomerDao;
import com.example.ecommerce.Room.Products.ProductDao;
import com.example.ecommerce.models.CustomerProducts;
import com.example.ecommerce.models.ProductModel;

public class InsertAsyncTask extends AsyncTask<CustomerProducts, Void, Void> {

    CustomerDao productDao;

    public InsertAsyncTask(CustomerDao productDao) {
        this.productDao = productDao;
    }

    @Override
    protected Void doInBackground(CustomerProducts... productModels) {
        productDao.insertProduct(productModels[0]);
        return null;
    }
}
