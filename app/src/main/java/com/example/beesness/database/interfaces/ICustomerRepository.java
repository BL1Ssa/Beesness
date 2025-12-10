package com.example.beesness.database.interfaces;

import com.example.beesness.models.Customer;
import com.example.beesness.utils.FirestoreCallback;

import java.util.List;

public interface ICustomerRepository{
    void add(Customer item, FirestoreCallback<Customer> callback);

    void getAll(FirestoreCallback<List<Customer>> callback);

    void getById(String id, FirestoreCallback<Customer> callback);

    void update(String id, Customer item, FirestoreCallback<Void> callback);

    void delete(String id, FirestoreCallback<Void> callback);
}
