package com.example.ecommerce.asyncTasks.CustomerProducts;

import android.os.AsyncTask;

import com.example.ecommerce.Room.Customer.CustomerDao;
import com.example.ecommerce.Room.Products.ProductDao;
import com.example.ecommerce.models.CustomerProducts;
import com.example.ecommerce.models.ProductModel;

import java.util.List;

public class GetAsyncTask extends AsyncTask<Void,Void, List<CustomerProducts>> {

    CustomerDao productDao;

    public GetAsyncTask(CustomerDao productDao) {
        this.productDao = productDao;
    }

    @Override
    protected List<CustomerProducts> doInBackground(Void... voids) {
        return productDao.getAllProducts();
    }
}
