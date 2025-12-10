package com.example.beesness.database.interfaces;

import com.example.beesness.models.ProductCategory;
import com.example.beesness.utils.FirestoreCallback;

import java.util.List;

public interface IProductCategoryRepository{
    void add(ProductCategory item, FirestoreCallback<ProductCategory> callback);

    void getAll(FirestoreCallback<List<ProductCategory>> callback);

    void getById(String id, FirestoreCallback<ProductCategory> callback);

    void update(String id, ProductCategory item, FirestoreCallback<Void> callback);

    void delete(String id, FirestoreCallback<Void> callback);
}
