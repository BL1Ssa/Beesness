package com.example.beesness.models;

public class Staff {
    // 1. The Identity
    private String id;
    private String userId;
    private String storeId;

    // 2. The Power
    private String role;        // "OWNER", "MANAGER", "CASHIER"

    // 3. The Status (HR Stuff)
    private boolean active;     // true = Hired, false = Fired/Suspended
    private String joinDate;    // "2023-12-01"
    private String email;       // Optional: Cache email here to show in "Staff List" quickly

    // Required for Firebase
    public Staff() {}

    public Staff(String id, String userId, String storeId, String role, String email, String joinDate) {
        this.id = id;
        this.userId = userId;
        this.storeId = storeId;
        this.role = role;
        this.email = email;
        this.joinDate = joinDate;
        this.active = true; // Default to active
    }

    // --- Getters and Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getStoreId() { return storeId; }
    public void setStoreId(String storeId) { this.storeId = storeId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getJoinDate() { return joinDate; }
    public void setJoinDate(String joinDate) { this.joinDate = joinDate; }
}
