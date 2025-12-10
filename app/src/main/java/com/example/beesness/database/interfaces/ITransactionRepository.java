package com.example.beesness.database.interfaces;

import com.example.beesness.models.Transaction;
import com.example.beesness.utils.FirestoreCallback;

import java.util.List;

public interface ITransactionRepository{
    void add(Transaction transaction, FirestoreCallback<Transaction> callback);

    void getAll(String storeId, FirestoreCallback<List<Transaction>> callback);

    void getById(String id, FirestoreCallback<Transaction> callback);


    void delete(String id, FirestoreCallback<Void> callback);
}
