package com.example.beesness.factories;

import com.example.beesness.models.Product;

public class ProductFactory {
    public static Product create(String name, double buyPrice, double sellPrice, String description, int image, String type, int quantity) {
        return new Product(null, name, buyPrice, sellPrice, description, image, type, quantity);
    }
}
