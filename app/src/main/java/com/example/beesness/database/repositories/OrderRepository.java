package com.example.beesness.database.repositories;

import com.example.beesness.database.interfaces.IOrderRepository;
import com.example.beesness.models.Order;
import com.example.beesness.utils.FirestoreCallback;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class OrderRepository implements IOrderRepository {
    private static OrderRepository instance;
    private final FirebaseFirestore db;
    private final CollectionReference ref;


    private OrderRepository() {
        db = FirebaseFirestore.getInstance();
        ref = db.collection("orders");
    }

    public static synchronized OrderRepository getInstance() {
        if (instance == null) {
            instance = new OrderRepository();
        }
        return instance;
    }

    @Override
    public void add(Order order, FirestoreCallback<Order> callback) {
        DocumentReference newDocRef = ref.document();
        order.setId(newDocRef.getId());

        newDocRef.set(order)
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess(order);
                }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void getAll(FirestoreCallback<List<Order>> callback) {
        ref.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Order> orderList = queryDocumentSnapshots.toObjects(Order.class);
            callback.onSuccess(orderList);
        }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void getById(String id, FirestoreCallback<Order> callback) {
        ref.document(id).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Order order = documentSnapshot.toObject(Order.class);
                    callback.onSuccess(order);
                }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void update(String id, Order order, FirestoreCallback<Void> callback) {
        ref.document(id).set(order)
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess(null);
                }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void delete(String id, FirestoreCallback<Void> callback) {
        ref.document(id).delete()
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess(null);
                }).addOnFailureListener(callback::onFailure);
    }
}
