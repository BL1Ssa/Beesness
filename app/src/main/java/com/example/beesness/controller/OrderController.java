package com.example.beesness.controller;

import com.example.beesness.database.repositories.OrderRepository;
import com.example.beesness.factories.OrderFactory;
import com.example.beesness.models.Order;
import com.example.beesness.models.Product;
import com.example.beesness.models.StoreCategory;
import com.example.beesness.models.User;
import com.example.beesness.utils.FirestoreCallback;
import com.example.beesness.utils.OperationCallback;
import com.example.beesness.utils.Result;

import java.util.List;

public class OrderController {
    private final OrderRepository repository;

    public OrderController(){
        repository = OrderRepository.getInstance();
    }

    public void add(String storeId, String staffId, String customerId, String customerName, List<Product> items, String paymentMethod, OperationCallback<Order> callback){
        callback.onResult(Result.loading());
        if(storeId.isEmpty() || staffId.isEmpty()){
            callback.onResult(Result.error("Store ID and Staff ID cannot be empty"));
            return;
        }
        if(customerId.isEmpty() || customerName.isEmpty()) {
            callback.onResult(Result.error("Customer ID and Name cannot be empty"));
            return;
        }
        if(items.isEmpty()){
            callback.onResult(Result.error("Items cannot be empty"));
            return;
        }
        if(paymentMethod.isEmpty()) {
            callback.onResult(Result.error("Payment Method cannot be empty"));
            return;
        }

        Order order = OrderFactory.create(storeId, staffId, customerId, customerName, items, paymentMethod);

        repository.add(order, new FirestoreCallback<Order>() {
            @Override
            public void onSuccess(Order result) {
                callback.onResult(Result.success(result, "Order added successfully"));
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    public void getAll(OperationCallback<List<Order>> callback){
        callback.onResult(Result.loading());
        repository.getAll(new FirestoreCallback<List<Order>>() {
            @Override
            public void onSuccess(List<Order> result) {
                if(result.isEmpty()){
                    callback.onResult(Result.success(result, "No orders found"));
                    return;
                }else {
                    callback.onResult(Result.success(result, "Orders fetched successfully"));
                    return;
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    public void getById(String id, OperationCallback<Order> callback){
        callback.onResult(Result.loading());
        if(id.isEmpty()){
            callback.onResult(Result.error("Order ID cannot be empty"));
            return;
        }
        repository.getById(id, new FirestoreCallback<Order>() {
            @Override
            public void onSuccess(Order result) {
                callback.onResult(Result.success(result, "Order fetched successfully"));
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    public void update(String id, String storeId, String staffId, String customerId, String customerName, List<Product> items, String paymentMethod, OperationCallback<Void> callback){
        callback.onResult(Result.loading());
        if(id.isEmpty()){
            callback.onResult(Result.error("Order ID cannot be empty"));
            return;
        }
        if(storeId.isEmpty() || staffId.isEmpty()) {
            callback.onResult(Result.error("Store ID and Staff ID cannot be empty"));
        }
        if(customerId.isEmpty() || customerName.isEmpty()) {
            callback.onResult(Result.error("Customer ID and Name cannot be empty"));
        }
        if(items.isEmpty()){
            callback.onResult(Result.error("Items cannot be empty"));
        }
        if(paymentMethod.isEmpty()) {
            callback.onResult(Result.error("Payment Method cannot be empty"));
            return;
        }
        Order order = OrderFactory.create(id, storeId, staffId, customerId, customerName, items, paymentMethod);
        repository.update(id, order, new FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                callback.onResult(Result.success(result, "Order updated successfully"));
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    public void delete(String id, OperationCallback<Void> callback){
        callback.onResult(Result.loading());
        if(id.isEmpty()){
            callback.onResult(Result.error("Order ID cannot be empty"));
            return;
        }
        repository.delete(id, new FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                callback.onResult(Result.success(result, "Order deleted successfully"));
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }
}
