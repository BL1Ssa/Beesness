package com.example.beesness.database.interfaces;

import com.example.beesness.models.Store;
import com.example.beesness.utils.FirestoreCallback;

import java.util.List;

public interface IStoreRepository{
    // Refactored: Removed 'Staff' parameter and removed the Transaction.
    // Since we are only writing to 'stores' now, a transaction is unnecessary overhead.
    void createStore(Store store, FirestoreCallback<Store> callback);

    void add(Store item, FirestoreCallback<Store> callback);

    void getAll(FirestoreCallback<List<Store>> callback);

    void getById(String id, FirestoreCallback<Store> callback);

    void update(String id, Store item, FirestoreCallback<Void> callback);

    void delete(String id, FirestoreCallback<Void> callback);
}

