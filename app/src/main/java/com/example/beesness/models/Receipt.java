package com.example.beesness.models;

import java.util.Date;
import java.util.List;

public class Receipt {

    // 1. Header (Store Info)
    private final String storeName;
    private final String storeAddress;

    // 2. Transaction Info
    private final String orderId;
    private final Date date;
    private final String cashierName;
    private final List<Product> items;

    private final double grandTotal;

    public Receipt(Store store, Order order, User owner) {
        this.storeName = store.getName();
        this.storeAddress = store.getAddress();

        this.orderId = order.getId();
        this.date = order.getDate();
        this.cashierName = (owner != null) ? owner.getName() : "Owner";

        this.items = order.getItems();

        this.grandTotal = order.getTotalRevenue();
    }

    public String getStoreName() { return storeName; }
    public String getStoreAddress() { return storeAddress; }
    public String getOrderId() { return orderId; }
    public Date getDate() { return date; }
    public String getCashierName() { return cashierName; }
    public List<Product> getItems() { return items; }
    public double getGrandTotal() { return grandTotal; }
}