package com.example.beesness.factories;

import com.example.beesness.models.Product;

public class ProductFactory {
    public static Product create(String storeId, String name, double buyPrice, double sellPrice, String description, String image, String type, int quantity) {
        return new Product(null, storeId, name, buyPrice, sellPrice, description, image, type, quantity);
    }
    public static Product create(String id, String storeId, String name, double buyPrice, double sellPrice, String description, String image, String type, int quantity) {
        return new Product(id, storeId, name, buyPrice, sellPrice, description, image, type, quantity);
    }
}
