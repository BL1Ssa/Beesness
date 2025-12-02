package com.example.beesness.models;

import java.util.Date;
import java.util.List;

public class Receipt {

    // 1. Header (Store Info)
    private String storeName;
    private String storeAddress;

    // 2. Transaction Info
    private String orderId;
    private Date date;
    private String cashierName;

    // 3. Line Items
    private List<Product> items;

    // 4. Totals
    private double grandTotal; // The "Total Price"

    // Constructor: Maps the ERP data to the Receipt format
    public Receipt(Store store, Order order, Staff cashier) {
        this.storeName = store.getName();
        this.storeAddress = store.getAddress();

        this.orderId = order.getId();
        this.date = order.getDate();
        this.cashierName = (cashier != null) ? cashier.getRole() : "Staff"; // Ideally use cashier.getName() if available

        this.items = order.getItems();

        // --- THE FIX IS HERE ---
        // Map 'totalRevenue' (ERP term) to 'grandTotal' (Receipt term)
        this.grandTotal = order.getTotalRevenue();
    }

    // --- Getters ---
    public String getStoreName() { return storeName; }
    public String getStoreAddress() { return storeAddress; }
    public String getOrderId() { return orderId; }
    public Date getDate() { return date; }
    public String getCashierName() { return cashierName; }
    public List<Product> getItems() { return items; }
    public double getGrandTotal() { return grandTotal; }
}