package com.example.beesness.controller.controllers;

import com.example.beesness.database.repositories.ProductRepository;
import com.example.beesness.utils.Result;

public class ProductController {
    private final ProductRepository repository;

    public ProductController(){
        repository = ProductRepository.getInstance();
    }

    public interface OperationCallback<T>{
        void onResult(Result<T> result);
    }

    public void add(String name, String price, String description, int image, String productType, int quantity, OperationCallback<String> callback){
        callback.onResult(Result.loading());

        if(name.isEmpty() || price.isEmpty()) {
            callback.onResult(Result.error("Name and price cannot be empty"));
            return;
        }
    }
}
