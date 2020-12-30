package com.example.ecommerce.asyncTasks.Product;

import android.os.AsyncTask;

import com.example.ecommerce.Room.Products.ProductDao;
import com.example.ecommerce.models.ProductModel;

public class DeleteProductAsyncTask extends AsyncTask<ProductModel, Void, Void> {

    ProductDao productDao;

    public DeleteProductAsyncTask(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    protected Void doInBackground(ProductModel... productModels) {
        productDao.deleteProduct(productModels[0]);
        return null;
    }
}
