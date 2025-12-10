package com.example.beesness.database.interfaces;

import com.example.beesness.models.StoreCategory;
import com.example.beesness.utils.FirestoreCallback;

import java.util.List;

public interface IStoreCategoryRepository{
    void add(StoreCategory item, FirestoreCallback<StoreCategory> callback);

    void getAll(FirestoreCallback<List<StoreCategory>> callback);

    void getById(String id, FirestoreCallback<StoreCategory> callback);

    void update(String id, StoreCategory item, FirestoreCallback<Void> callback);

    void delete(String id, FirestoreCallback<Void> callback);
}
