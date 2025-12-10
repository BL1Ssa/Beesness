package com.example.beesness.database.interfaces;

import com.example.beesness.models.Product;
import com.example.beesness.utils.FirestoreCallback;

import java.util.List;

public interface IProductRepository{
    void getAllByStoreId(String id, FirestoreCallback<List<Product>> callback);

    void add(Product item, FirestoreCallback<Product> callback);

    void getAll(FirestoreCallback<List<Product>> callback);

    void getById(String id, FirestoreCallback<Product> callback);

    void update(String id, Product item, FirestoreCallback<Void> callback);

    void delete(String id, FirestoreCallback<Void> callback);
}
