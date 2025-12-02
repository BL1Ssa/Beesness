package com.example.beesness.models;

import com.example.beesness.models.enums.OrderType;

import java.util.ArrayList;

public class Order {
    private String id;
    private String userId;
    private ArrayList<Product> products;
    private OrderType orderType;
    public Order(){}

    public Order(String id, String userId, ArrayList<Product> products, OrderType orderType) {
        this.id = id;
        this.userId = userId;
        this.products = products;
        this.orderType = orderType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }
}
