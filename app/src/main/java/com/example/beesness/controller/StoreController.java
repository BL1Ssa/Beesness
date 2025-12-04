package com.example.beesness.controller;

import com.example.beesness.utils.FirestoreCallback;
import com.example.beesness.database.repositories.StoreRepository;
import com.example.beesness.factories.StaffFactory;
import com.example.beesness.factories.StoreFactory;
import com.example.beesness.models.Staff;
import com.example.beesness.models.Store;
import com.example.beesness.models.StoreCategory;
import com.example.beesness.models.User;
import com.example.beesness.utils.OperationCallback;
import com.example.beesness.utils.Result;

import java.util.List;

public class StoreController {

    private final StoreRepository repository;

    public StoreController() {
        this.repository = StoreRepository.getInstance();
    }

    public void add(String name, String address, String phone, String currency, StoreCategory selectedCategory, User currentUser, OperationCallback<String> callback) {

        callback.onResult(Result.loading());

        if (selectedCategory == null) {
            callback.onResult(Result.error("Please select a business category"));
            return;
        }

        if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            callback.onResult(Result.error("All fields are required"));
            return;
        }

        if (currentUser == null || currentUser.getId() == null) {
            callback.onResult(Result.error("User session error. Please relogin."));
            return;
        }

        Store store = StoreFactory.create(name, address, phone, currentUser.getId(), currency, selectedCategory);

        Staff ownerStaff = StaffFactory.createOwner(currentUser);

        repository.createStore(store, ownerStaff, new FirestoreCallback<Store>() {
            @Override
            public void onSuccess(Store result) {
                callback.onResult(Result.success(result.getId(), "Store Created Successfully!"));
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    public void getByOwnerId(User user, OperationCallback<List<Store>> callback){
        callback.onResult(Result.loading());
        if (user == null || user.getId() == null) {
            callback.onResult(Result.error("User session invalid"));
            return;
        }
        repository.getByOwnerId(user.getId(), new FirestoreCallback<List<Store>>() {
            @Override
            public void onSuccess(List<Store> result) {
                if (!result.isEmpty()) callback.onResult(Result.success(result, "Store loaded successfully"));
                else callback.onResult(Result.success(result, "No stores found"));
            }
            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    public void getStoreById(String storeId, OperationCallback<Store> callback){
        callback.onResult(Result.loading());
        repository.getById(storeId, new FirestoreCallback<Store>() {
            @Override
            public void onSuccess(Store result) {
                callback.onResult(Result.success(result, "Store loaded successfully"));
            }
            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error("Store not found"));
            }
        });
    }

    public void update(String storeId, String name, String address, String phone, String currency, StoreCategory selectedCategory, User currentUser, OperationCallback<String> callback){
        callback.onResult(Result.loading());
        if(storeId.isEmpty()){
            callback.onResult(Result.error("Store id missing"));
            return;
        }
        if(name.isEmpty() || address.isEmpty() || phone.isEmpty() || currency.isEmpty() || selectedCategory == null){
            callback.onResult(Result.error("Store data cannot be empty"));
            return;
        }
        if(currentUser == null || currentUser.getId() == null) {
            callback.onResult(Result.error("User session error. Please relogin."));
            return;
        }

        Store store = StoreFactory.create(name, address, phone, currentUser.getId(), currency, selectedCategory);
        repository.update(storeId, store, new FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                callback.onResult(Result.success(storeId,"Store updated successfully"));
            }
            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    public void delete(String storeId, OperationCallback<String> callback){
        callback.onResult(Result.loading());

        if(storeId == null || storeId.isEmpty()){
            callback.onResult(Result.error("Invalid Store ID"));
            return;
        }

        repository.delete(storeId, new FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                callback.onResult(Result.success(storeId, "Deleted successfully"));
            }
            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }
}