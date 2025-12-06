package com.example.beesness.factories;

import com.example.beesness.models.Order;
import com.example.beesness.models.Product;

import java.util.List;

public class OrderFactory {

    //for create
    public static Order create(String storeId, String staffId, String customerId, String customerName, List<Product> items, String paymentMethod) {
        // Note: ID is null because the Repository generates it (ORD-2023...)
        return new Order(null, storeId, staffId, customerId, customerName, items, paymentMethod);
    }


    //for update
    public static Order create(String id, String storeId, String staffId, String customerId, String customerName, List<Product> items, String paymentMethod) {
        // Note: ID is null because the Repository generates it (ORD-2023...)
        return new Order(id, storeId, staffId, customerId, customerName, items, paymentMethod);
    }
}
