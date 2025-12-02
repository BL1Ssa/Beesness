package com.example.beesness.controller.controllers;

import com.example.beesness.database.repositories.ProductRepository;
import com.example.beesness.utils.OperationCallback;
import com.example.beesness.utils.Result;

public class ProductController {
    private final ProductRepository repository;

    public ProductController(){
        repository = ProductRepository.getInstance();
    }


    public void add(String name, String price, String description, int image, String productType, int quantity, OperationCallback<String> callback){
        callback.onResult(Result.loading());

        if(name.isEmpty() || price.isEmpty() || description.isEmpty() || image == 0 || productType.isEmpty() || quantity == 0) {
            callback.onResult(Result.error("Please fill in all fields"));
            return;
        }
    }


}
