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

public class StoreController {

    private final StoreRepository repository;

    public StoreController() {
        this.repository = StoreRepository.getInstance();
    }

    public void createStore(String name, String address, String phone, String currency, StoreCategory selectedCategory, User currentUser, OperationCallback<String> callback) {

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
}