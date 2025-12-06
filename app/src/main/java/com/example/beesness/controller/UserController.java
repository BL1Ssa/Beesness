package com.example.beesness.controller;

import com.example.beesness.database.repositories.UserRepository;
import com.example.beesness.factories.UserFactory;
import com.example.beesness.models.User;
import com.example.beesness.utils.FirestoreCallback;
import com.example.beesness.utils.OperationCallback;
import com.example.beesness.utils.Result;

import java.util.List;

public class UserController {
    private final UserRepository repository;

    public UserController(){
        repository = UserRepository.getInstance();
    }

    public void add(String name, String email, String phoneNumber, OperationCallback<User> callback){
        callback.onResult(Result.loading());
        if(name.isEmpty() || email.isEmpty() || phoneNumber.isEmpty()){
            callback.onResult(Result.error("All fields are required"));
            return;
        }
        User user = new User(null, name, email, phoneNumber);

        repository.add(user, new FirestoreCallback<User>() {
            @Override
            public void onSuccess(User result) {
                callback.onResult(Result.success(result, "User added successfully"));
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    public void getAll(OperationCallback<List<User>> callback) {
        callback.onResult(Result.loading());

        repository.getAll(new FirestoreCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> result) {
                if(result.isEmpty()){
                    callback.onResult(Result.success(result, "No users found"));
                    return;
                }
                else {
                    callback.onResult(Result.success(result, "Users fetched successfully"));
                    return;
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    public void getById(String id, OperationCallback<User> callback){
        callback.onResult(Result.loading());
        if(id.isEmpty()) {
            callback.onResult(Result.error("User ID cannot be empty"));
            return;
        }
        repository.getById(id, new FirestoreCallback<User>() {
            @Override
            public void onSuccess(User result) {
                callback.onResult(Result.success(result, "User fetched successfully"));
            }
            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    public void update(String id, String name, String email, String phoneNumber, OperationCallback<Void> callback){
        callback.onResult(Result.loading());
        if(id.isEmpty()) {
            callback.onResult(Result.error("User ID cannot be empty"));
            return;
        }
        if(name.isEmpty() || email.isEmpty() || phoneNumber.isEmpty()){
            callback.onResult(Result.error("All fields are required"));
            return;
        }
        User user = UserFactory.create(id, name, email, phoneNumber);
        repository.update(id, user, new FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                callback.onResult(Result.success(result, "User updated successfully"));
            }
            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    public void delete(String id, OperationCallback<Void> callback){
        callback.onResult(Result.loading());
        if(id.isEmpty()) {
            callback.onResult(Result.error("User ID cannot be empty"));
            return;
        }
        repository.delete(id, new FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                callback.onResult(Result.success(result, "User deleted successfully"));
            }
            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
        }
    }
}
