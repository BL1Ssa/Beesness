package com.example.beesness.controller;

import com.example.beesness.database.repositories.StaffRepository;
import com.example.beesness.factories.StaffFactory;
import com.example.beesness.models.Staff;
import com.example.beesness.utils.FirestoreCallback;
import com.example.beesness.utils.OperationCallback;
import com.example.beesness.utils.Result;

import java.util.List;

public class StaffController {
    private StaffRepository repository;

    public StaffController(){
        repository = StaffRepository.getInstance();
    }

    public void add(String userId, String userEmail, String storeId, String role, OperationCallback<Staff> callback){
        callback.onResult(Result.loading());
        if(userId.isEmpty() || userEmail.isEmpty() || storeId.isEmpty() || role.isEmpty()){
            callback.onResult(Result.error("All fields must be filled"));
        }

        Staff staff = StaffFactory.create(userId, userEmail, storeId, role);

        repository.add(staff, new FirestoreCallback<Staff>() {
            @Override
            public void onSuccess(Staff result) {
                callback.onResult(Result.success(staff, "Staff Added"));
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    public void getAll(OperationCallback<List<Staff>> callback) {
        callback.onResult(Result.loading());
        repository.getAll(new FirestoreCallback<List<Staff>>() {
            @Override
            public void onSuccess(List<Staff> result) {
                if(result.isEmpty()){
                    callback.onResult(Result.success(result, "No staff found"));
                }
                else {
                    callback.onResult(Result.success(result, "Loaded " + result.size() + " staff"));
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    public void getById(String id, OperationCallback<Staff> callback){
        callback.onResult(Result.loading());
        if(id.isEmpty()){
            callback.onResult(Result.error("Staff id missing"));
            return;
        }

        repository.getById(id, new FirestoreCallback<Staff>() {
            @Override
            public void onSuccess(Staff result) {
                callback.onResult(Result.success(result, "Staff loaded successfully"));
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    public void update(String id, String userId, String userEmail, String storeId, String role, OperationCallback<String> callback) {
        callback.onResult(Result.loading());
        if(id.isEmpty()){
            callback.onResult(Result.error("Staff id missing"));
            return;
        }
        if(userId.isEmpty() || userEmail.isEmpty() || storeId.isEmpty() || role.isEmpty()){
            callback.onResult(Result.error("All fields must be filled"));
            return;
        }

        Staff staff = StaffFactory.create(id, userId, userEmail, storeId, role);

        repository.update(id, staff, new FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                callback.onResult(Result.success(id, "Staff Updated"));
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    public void delete(String id, OperationCallback<String> callback){
        callback.onResult(Result.loading());
        if(id.isEmpty()){
            callback.onResult(Result.error("Staff id missing"));
            return;
        }
        repository.delete(id, new FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                callback.onResult(Result.success(id, "Staff deleted"));
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }
}
