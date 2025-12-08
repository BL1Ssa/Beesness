package com.example.beesness.controller;

import com.example.beesness.database.repositories.CustomerRepository;
import com.example.beesness.factories.CustomerFactory;
import com.example.beesness.models.Customer;
import com.example.beesness.utils.FirestoreCallback;
import com.example.beesness.utils.OperationCallback;
import com.example.beesness.utils.Result;

import org.checkerframework.checker.units.qual.C;

import java.util.List;

public class CustomerController {
    private final CustomerRepository repository;

    public CustomerController() {
        this.repository = CustomerRepository.getInstance();
    }

    public void add(String name, String email, String address, String phoneNumber, String storeId, OperationCallback<Customer> callback){
        callback.onResult(Result.loading());
        if(name.isEmpty() || email.isEmpty() || address.isEmpty() || phoneNumber.isEmpty() || storeId.isEmpty()){
            callback.onResult(Result.error("All fields are required"));
            return;
        }

        Customer customer = CustomerFactory.create(name, email, address, phoneNumber, storeId);

        repository.add(customer, new FirestoreCallback<Customer>() {
            @Override
            public void onSuccess(Customer result) {
                callback.onResult(Result.success(result, "Customer added successfully"));
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    public void getAll(OperationCallback<List<Customer>> callback){
        callback.onResult(Result.loading());
        repository.getAll(new FirestoreCallback<List<Customer>>() {
            @Override
            public void onSuccess(List<Customer> result) {
                if(result.isEmpty()){
                    callback.onResult(Result.success(result, "No customers found"));
                    return;
                }else {
                    callback.onResult(Result.success(result, "Customers fetched successfully"));
                    return;
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    public void getById(String id, OperationCallback<Customer> callback){
        callback.onResult(Result.loading());
        if(id.isEmpty()) {
            callback.onResult(Result.error("Customer ID cannot be empty"));
            return;
        }
        repository.getById(id, new FirestoreCallback<Customer>() {
            @Override
            public void onSuccess(Customer result) {
                callback.onResult(Result.success(result, "Customer fetched successfully"));
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    public void update(String id, String name, String email, String address, String phoneNumber, String storeId, OperationCallback<Void> callback){
        callback.onResult(Result.loading());
        if(id.isEmpty()) {
            callback.onResult(Result.error("Customer ID cannot be empty"));
            return;
        }
        if(name.isEmpty() || email.isEmpty() || address.isEmpty() || phoneNumber.isEmpty() || storeId.isEmpty()){
            callback.onResult(Result.error("All fields are required"));
            return;
        }
        Customer customer = CustomerFactory.create(id, name, email, address, phoneNumber, storeId);
        repository.update(id, customer, new FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                callback.onResult(Result.success(result, "Customer updated successfully"));
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    public void delete(String id, OperationCallback<Void> callback){
        callback.onResult(Result.loading());
        if(id.isEmpty()) {
            callback.onResult(Result.error("Customer ID cannot be empty"));
            return;
        }
        repository.delete(id, new FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                callback.onResult(Result.success(result, "Customer deleted successfully"));
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }
}
