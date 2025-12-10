package com.example.beesness.models;

import java.io.Serializable;
import java.util.Date;

public class Transaction implements Serializable {
    private String id;
    private String type;
    private double totalAmount;
    private Date date;
    private String summary;

    public Transaction() {}

    public Transaction(String id, String type, double totalAmount, String summary) {
        this.id = id;
        this.type = type;
        this.totalAmount = totalAmount;
        this.summary = summary;
        this.date = new Date();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getType() { return type; }
    public double getTotalAmount() { return totalAmount; }
    public Date getDate() { return date; }
    public String getSummary() { return summary; }
}