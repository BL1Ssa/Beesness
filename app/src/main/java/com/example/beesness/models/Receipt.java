package com.example.beesness.models;

import com.google.type.DateTime;

import java.util.ArrayList;

public class Receipt {
    private String id;
    private DateTime time;
    private String userId;
    private String orderId;

    public Receipt(){}

    public Receipt(String id, DateTime time, String userId, String orderId) {
        this.id = id;
        this.time = time;
        this.userId = userId;
        this.orderId = orderId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTime(DateTime time) {
        this.time = time;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getId() {
        return id;
    }

    public DateTime getTime() {
        return time;
    }

    public String getUserId() {
        return userId;
    }

    public String getOrderId() {
        return orderId;
    }
}
