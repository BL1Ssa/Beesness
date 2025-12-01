package com.example.beesness.models;

import java.util.ArrayList;

public class Order {
    private String id;
    private String userId;
    private ArrayList<Product> products;

    public Order(){}

    public Order(String id, String userId, ArrayList<Product> products) {
        this.id = id;
        this.userId = userId;
        this.products = products;
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
}
