package com.example.beesness.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
    private String id;
    private String storeId;      // Which store sold this?
    private String staffId;      // Which cashier sold this?
    private String customerId;   // Who bought it? (Optional)
    private String customerName; // Snapshot of name (so we don't have to fetch Customer again)

    private Date date;           // When did it happen?
    private List<Product> items; // The list of items sold

    // --- Financials (The most important part for ERP) ---
    private double totalRevenue; // What the customer paid
    private double totalCost;    // What it cost you (for calculating Profit)
    private String paymentMethod;// "Cash", "Transfer", "QRIS"

    // 1. Empty Constructor
    public Order() {
        this.items = new ArrayList<>();
        this.date = new Date(); // Default to "now"
    }

    // 2. Main Constructor
    public Order(String id, String storeId, String staffId, String customerId, String customerName, List<Product> items, String paymentMethod) {
        this.id = id;
        this.storeId = storeId;
        this.staffId = staffId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.items = items;
        this.paymentMethod = paymentMethod;
        this.date = new Date();

        calculateTotals(); // Auto-calculate money
    }

    // --- Logic: Auto-Calculate Financials ---
    // We do this here so we never save wrong math to the database
    public void calculateTotals() {
        this.totalRevenue = 0;
        this.totalCost = 0;

        if (items != null) {
            for (Product p : items) {
                // In the context of an Order, p.getQuantity() means "Qty Sold"
                double qty = p.getQuantity();

                this.totalRevenue += (p.getSellPrice() * qty);
                this.totalCost += (p.getBuyPrice() * qty);
            }
        }
    }

    // Helper: Profit Calculation (Real-time)
    // We don't strictly need to save this, we can derive it from (Revenue - Cost)
    public double getProfit() {
        return totalRevenue - totalCost;
    }

    // --- Getters and Setters ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStoreId() { return storeId; }
    public void setStoreId(String storeId) { this.storeId = storeId; }

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public List<Product> getItems() { return items; }
    public void setItems(List<Product> items) {
        this.items = items;
        calculateTotals(); // Recalculate if items change
    }

    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }

    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}