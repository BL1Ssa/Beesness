package com.example.beesness.controller;

import com.example.beesness.R;
import com.example.beesness.database.repositories.ProductCategoryRepository;
import com.example.beesness.factories.ProductCategoryFactory;
import com.example.beesness.models.ProductCategory;
import com.example.beesness.utils.FirestoreCallback;
import com.example.beesness.utils.OperationCallback;
import com.example.beesness.utils.Result;

import java.util.List;

public class ProductCategoryController {
    private final ProductCategoryRepository repository;

    public ProductCategoryController(){
        repository = ProductCategoryRepository.getInstance();
    }

    public void add(String name, String code, OperationCallback<String> callback){
        callback.onResult(Result.loading());
        if(name.isEmpty() || code.isEmpty()) {
            callback.onResult(Result.error("All fields are required"));
            return;
        }

        ProductCategory productCategory = ProductCategoryFactory.create(name, code);

        repository.add(productCategory, new FirestoreCallback<ProductCategory>() {
            @Override
            public void onSuccess(ProductCategory result) {
                callback.onResult(Result.success(productCategory.getId(), "Product Category Added"));
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    public void getAll(OperationCallback<List<ProductCategory>> callback){
        callback.onResult(Result.loading());
        repository.getAll(new FirestoreCallback<List<ProductCategory>>() {
            @Override
            public void onSuccess(List<ProductCategory> result) {
                if(result.isEmpty()){
                    callback.onResult(Result.success(result, "No product categories found"));
                }
                else {
                    callback.onResult(Result.success(result, "Loaded " + result.size() + " product categories"));
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    public void getById(String id, OperationCallback<ProductCategory> callback){
        callback.onResult(Result.loading());
        repository.getById(id, new FirestoreCallback<ProductCategory>() {
            @Override
            public void onSuccess(ProductCategory result) {
                callback.onResult(Result.success(result, "ProductCategory Loaded Successfully"));
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    public void update(String id, String name, String code, OperationCallback<String> callback){
        callback.onResult(Result.loading());
        if (id == null || id.isEmpty()) {
            callback.onResult(Result.error("Category ID is missing"));
            return;
        }
        if(name.isEmpty() || code.isEmpty()) {
            callback.onResult(Result.error("All fields are required"));
            return;
        }

        ProductCategory productCategory = ProductCategoryFactory.create(id, name, code);

        repository.update(id, productCategory, new FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                callback.onResult(Result.success(productCategory.getId(), "ProductCategory Updated"));
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    public void delete(String id, OperationCallback<String> callback){
        callback.onResult(Result.loading());

        if (id == null || id.isEmpty()) {
            callback.onResult(Result.error("Invalid Product ID"));
            return;
        }

        repository.delete(id, new FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                callback.onResult(Result.success(id, "ProductCategory Deleted"));
            }
            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }
}