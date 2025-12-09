package com.example.beesness.controller;

import com.example.beesness.database.repositories.StoreCategoryRepository;
import com.example.beesness.factories.StoreCategoryFactory;
import com.example.beesness.models.StoreCategory;
import com.example.beesness.utils.FirestoreCallback;
import com.example.beesness.utils.OperationCallback;
import com.example.beesness.utils.Result;

import java.util.List;

public class StoreCategoryController {
    private final StoreCategoryRepository repository;

    public StoreCategoryController() {
        this.repository = StoreCategoryRepository.getInstance();
    }

    public void add(String name, OperationCallback<StoreCategory> callback){
        callback.onResult(Result.loading());
        if(name.isEmpty()){
            callback.onResult(Result.error("All fields must be filled"));
            return;
        }

        StoreCategory storeCategory = StoreCategoryFactory.create(name);

        repository.add(storeCategory, new FirestoreCallback<StoreCategory>() {
            @Override
            public void onSuccess(StoreCategory result) {
                callback.onResult(Result.success(result, "Store Category Created"));
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    public void getAll(OperationCallback<List<StoreCategory>> callback){
        callback.onResult(Result.loading());
        repository.getAll(new FirestoreCallback<List<StoreCategory>>() {
            @Override
            public void onSuccess(List<StoreCategory> result) {
                if(result.isEmpty()){
                    callback.onResult(Result.success(result, "No Store Categories Found"));
                }else{
                    callback.onResult(Result.success(result, "Store Categories Found"));
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    public void getById(String id, OperationCallback<StoreCategory> callback){
        callback.onResult(Result.loading());
        if(id.isEmpty()){
            callback.onResult(Result.error("All fields must be filled"));
            return;
        }

        repository.getById(id, new FirestoreCallback<StoreCategory>() {
            @Override
            public void onSuccess(StoreCategory result) {
                callback.onResult(Result.success(result, "Store Category Found"));
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    public void update(String id, String name, OperationCallback<Void> callback){
        callback.onResult(Result.loading());
        if(id.isEmpty()){
            callback.onResult(Result.error("Invalid: ID not found"));
            return;
        }
        if(name.isEmpty()) {
            callback.onResult(Result.error("All fields must be filled"));
        }

        StoreCategory storeCategory = StoreCategoryFactory.create(id, name);

        repository.update(id, storeCategory, new FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                callback.onResult(Result.success(result, "Store Category Updated"));
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    public void delete(String id, OperationCallback<Void> callback) {
        callback.onResult(Result.loading());
        if (id.isEmpty()) {
            callback.onResult(Result.error("Invalid: ID not found"));
            return;
        }
        repository.delete(id, new FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                callback.onResult(Result.success(result, "Store Category Deleted"));
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }
}
