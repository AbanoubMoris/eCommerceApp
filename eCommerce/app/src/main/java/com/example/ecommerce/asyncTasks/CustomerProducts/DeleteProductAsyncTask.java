package com.example.ecommerce.asyncTasks.CustomerProducts;

import android.os.AsyncTask;

import com.example.ecommerce.Room.Customer.CustomerDao;
import com.example.ecommerce.Room.Products.ProductDao;
import com.example.ecommerce.models.CustomerProducts;
import com.example.ecommerce.models.ProductModel;

public class DeleteProductAsyncTask extends AsyncTask<CustomerProducts, Void, Void> {

    CustomerDao productDao;

    public DeleteProductAsyncTask(CustomerDao productDao) {
        this.productDao = productDao;
    }

    @Override
    protected Void doInBackground(CustomerProducts... customerProducts) {
        productDao.deleteProduct(customerProducts[0]);
        return null;
    }
}
