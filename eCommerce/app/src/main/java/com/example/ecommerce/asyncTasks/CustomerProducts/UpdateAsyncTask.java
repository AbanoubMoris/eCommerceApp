package com.example.ecommerce.asyncTasks.CustomerProducts;

import android.os.AsyncTask;

import com.example.ecommerce.Room.Customer.CustomerDao;
import com.example.ecommerce.Room.Products.ProductDao;
import com.example.ecommerce.models.CustomerProducts;
import com.example.ecommerce.models.ProductModel;

public class UpdateAsyncTask extends AsyncTask<CustomerProducts, Void, Void> {

    CustomerDao productDao;

    public UpdateAsyncTask(CustomerDao productDao) {
        this.productDao = productDao;
    }

    @Override
    protected Void doInBackground(CustomerProducts... productModels) {
        productDao.updateProduct(productModels[0]);
        return null;
    }
}
