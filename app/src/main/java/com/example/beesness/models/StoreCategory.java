package com.example.beesness.models;

public class StoreCategory {
    private String id;
    private String name;    // "Food & Beverage"
    private String code;    // "FNB"

    public StoreCategory() {}

    public StoreCategory(String id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    // --- Getters & Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}