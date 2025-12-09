package com.example.beesness.controller;

import android.net.Uri;

import com.example.beesness.utils.FirestoreCallback;
import com.example.beesness.database.repositories.ProductRepository;
import com.example.beesness.factories.ProductFactory;
import com.example.beesness.models.Product;
import com.example.beesness.models.ProductCategory;
import com.example.beesness.utils.OperationCallback;
import com.example.beesness.utils.Result;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.UUID;

public class ProductController {

    private final ProductRepository repository;

    public ProductController() {
        this.repository = ProductRepository.getInstance();
    }

    public void add(String storeId, String name, String buyPriceStr, String sellPriceStr, String description,
                    String quantityStr, ProductCategory category, String image,
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

        if (image != null && !image.isEmpty()) {

            // 1. Create a reference to Firebase Storage
            // "product_images/" is the folder name in the cloud
            String filename = UUID.randomUUID().toString(); // Random name (e.g., "abc-123-xyz")
            StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                    .child("product_images/" + filename);

            Uri fileUri = Uri.parse(image);

            // 2. Upload the file
            storageRef.putFile(fileUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // 3. Upload Success! Now get the REAL link (https://...)
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String cloudUrl = uri.toString();

                            // 4. Save Product with the Cloud URL
                            Product product = ProductFactory.create(storeId, name, buyPrice, sellPrice, description, cloudUrl, category.getName(), quantity);
                            saveToFirestore(product, category.getCode(), callback);
                        });
                    })
                    .addOnFailureListener(e -> {
                        callback.onResult(Result.error("Image Upload Failed: " + e.getMessage()));
                    });

        } else {
            // No Image selected? Save with empty string
            Product product = ProductFactory.create(storeId, name, buyPrice, sellPrice, description, "", category.getName(), quantity);
            saveToFirestore(product, category.getCode(), callback);
        }
    }

    public void getAll(String storeId, OperationCallback<List<Product>> callback) {
        callback.onResult(Result.loading());

        repository.getAllByStoreId(storeId, new FirestoreCallback<List<Product>>() {
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

    public void getById(String id, OperationCallback<Product> callback){
        callback.onResult(Result.loading());
        repository.getById(id, new FirestoreCallback<Product>() {
            @Override
            public void onSuccess(Product result) {
                callback.onResult(Result.success(result, "Product loaded successfully"));
            }
            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    public void update(String productId, String storeId, String name, String buyPriceStr, String sellPriceStr, String description,
                       String quantityStr, ProductCategory category, String image,
                       OperationCallback<String> callback) {
        callback.onResult(Result.loading());
        if(productId.isEmpty()){
            callback.onResult(Result.error("Product id missing"));
            return;
        }
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

        Product product = ProductFactory.create(productId, storeId, name, buyPrice, sellPrice, description, image, category.getName(), quantity);

        repository.update(productId, product, new FirestoreCallback<Void>() {
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

    private void saveToFirestore(Product product, String categoryCode, OperationCallback<String> callback) {
        repository.add(product, categoryCode, new FirestoreCallback<Product>() {
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
}