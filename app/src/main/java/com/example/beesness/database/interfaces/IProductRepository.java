package com.example.beesness.database.interfaces;

import com.example.beesness.models.Product;
import com.example.beesness.models.User;
import com.example.beesness.utils.FirestoreCallback;

import java.util.List;

public interface IProductRepository extends IBaseRepository<Product>{
    // =================================================================
    // THE CUSTOM ID GENERATOR
    // =================================================================
    void add(Product product, String categoryCode, FirestoreCallback<Product> callback);
}
