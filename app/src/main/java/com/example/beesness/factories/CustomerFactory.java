package com.example.beesness.factories;

import com.example.beesness.models.Customer;

public class CustomerFactory {
    //for create
    public static Customer create(String name, String email, String address, String phoneNumber, String storeId) {
        return new Customer(null, name, email, address, phoneNumber, storeId);
    }

    //for update
    public static Customer create(String id, String name, String email, String address, String phoneNumber, String storeId) {
        return new Customer(id, name, email, address, phoneNumber, storeId);
    }
}
