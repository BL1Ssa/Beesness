package com.example.beesness.models;

public class Store {
    private String id;
    private String name;
    private String address;
    private String phone;
    private String ownerId;
    private String currency;

    // CHANGED: Clearer naming. Stores "FNB", "RTL", etc.
    private String categoryId;

    public Store() {}

    public Store(String id, String name, String address, String phone, String ownerId, String currency, String categoryId) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.ownerId = ownerId;
        this.currency = currency;
        this.categoryId = categoryId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}