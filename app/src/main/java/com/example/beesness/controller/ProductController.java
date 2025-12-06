package com.example.beesness.controller;

import com.example.beesness.utils.FirestoreCallback;
import com.example.beesness.database.repositories.ProductRepository;
import com.example.beesness.factories.ProductFactory;
import com.example.beesness.models.Product;
import com.example.beesness.models.ProductCategory;
import com.example.beesness.utils.OperationCallback;
import com.example.beesness.utils.Result;

import java.util.List;

public class ProductController {

    private final ProductRepository repository;

    public ProductController() {
        this.repository = ProductRepository.getInstance();
    }

    public void add(String name, String buyPriceStr, String sellPriceStr, String description,
                    String quantityStr, ProductCategory category, int image,
                    OperationCallback<String> callback) {

        callback.onResult(Result.loading());

        if (category == null) {
            callback.onResult(Result.error("Please select a product category"));
            return;
        }
        if (name.isEmpty() || buyPriceStr.isEmpty() || sellPriceStr.isEmpty() || description.isEmpty() || quantityStr.isEmpty()) {
            callback.onResult(Result.error("All fields are required"));
            return;
        }

        double buyPrice, sellPrice;
        int quantity;
        try {
            buyPrice = Double.parseDouble(buyPriceStr);
            sellPrice = Double.parseDouble(sellPriceStr);
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            callback.onResult(Result.error("Price and Quantity must be valid numbers"));
            return;
        }
        if (buyPrice < 0 || sellPrice < 0 || quantity < 0) {
            callback.onResult(Result.error("Values cannot be negative"));
            return;
        }

        Product product = ProductFactory.create(name, buyPrice, sellPrice, description, image, category.getName(), quantity);

        repository.add(product, category.getCode(), new FirestoreCallback<Product>() {
            @Override
            public void onSuccess(Product result) {
                callback.onResult(Result.success(result.getId(), "Product Added Successfully!"));
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    public void getAll(OperationCallback<List<Product>> callback) {
        callback.onResult(Result.loading());

        repository.getAll(new FirestoreCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> result) {
                if (result.isEmpty()) {
                    callback.onResult(Result.success(result, "No products found"));
                } else {
                    callback.onResult(Result.success(result, "Loaded " + result.size() + " products"));
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    public void update(Product product, OperationCallback<String> callback) {
        callback.onResult(Result.loading());

        if (product.getId() == null || product.getId().isEmpty()) {
            callback.onResult(Result.error("Cannot update: Missing Product ID"));
            return;
        }

        repository.update(product.getId(), product, new FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                callback.onResult(Result.success(product.getId(), "Product Updated"));
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    public void delete(String productId, OperationCallback<String> callback) {
        callback.onResult(Result.loading());

        if (productId == null || productId.isEmpty()) {
            callback.onResult(Result.error("Invalid Product ID"));
            return;
        }

        repository.delete(productId, new FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                callback.onResult(Result.success(productId, "Product Deleted"));
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }
}