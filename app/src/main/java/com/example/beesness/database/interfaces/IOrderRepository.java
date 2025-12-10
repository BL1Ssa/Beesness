package com.example.beesness.database.interfaces;

import com.example.beesness.models.Order;
import com.example.beesness.utils.FirestoreCallback;

import java.util.List;

public interface IOrderRepository{
    void add(Order item, FirestoreCallback<Order> callback);

    void getAll(FirestoreCallback<List<Order>> callback);

    void getById(String id, FirestoreCallback<Order> callback);

    void update(String id, Order item, FirestoreCallback<Void> callback);

    void delete(String id, FirestoreCallback<Void> callback);
// speculative generality :v
}
