package com.example.beesness.database.interfaces;

import com.example.beesness.models.Product;
import com.example.beesness.utils.FirestoreCallback;

import java.util.List;

public interface IProductRepository extends IBaseRepository<Product>{
    void getAllByStoreId(String id, FirestoreCallback<List<Product>> callback);
}
