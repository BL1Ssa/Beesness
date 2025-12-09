package com.example.beesness.controller;

import com.example.beesness.utils.FirestoreCallback;
import com.example.beesness.database.repositories.AuthRepository;
import com.example.beesness.models.User;
import com.example.beesness.utils.OperationCallback;
import com.example.beesness.utils.Result;

public class AuthController {

    private final AuthRepository repository;

    public AuthController() {
        this.repository = AuthRepository.getInstance();
    }

    public void login(String email, String password, OperationCallback<User> callback) {
        callback.onResult(Result.loading());

        if (email.isEmpty() || password.isEmpty()) {
            callback.onResult(Result.error("Email and Password required"));
            return;
        }

        repository.login(email, password, new FirestoreCallback<User>() {
            @Override
            public void onSuccess(User result) {
                callback.onResult(Result.success(result, "Welcome back, " + result.getName()));
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    public void register(String name, String email, String phonenum, String password, String confirmPassword, OperationCallback<User> callback) {
        callback.onResult(Result.loading());

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phonenum.isEmpty()) {
            callback.onResult(Result.error("All fields are required"));
            return;
        }
        if (!password.equals(confirmPassword)) {
            callback.onResult(Result.error("Passwords do not match"));
            return;
        }
        if (password.length() < 6) {
            callback.onResult(Result.error("Password must be at least 6 characters"));
            return;
        }

        repository.register(email, password, name, phonenum, new FirestoreCallback<User>() {
            @Override
            public void onSuccess(User result) {
                callback.onResult(Result.success(result, "Account created successfully!"));
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    // Check if user is already logged in (Splash Screen logic)
    public void checkSession(OperationCallback<User> callback) {
        repository.getCurrentUser(new FirestoreCallback<User>() {
            @Override
            public void onSuccess(User result) {
                callback.onResult(Result.success(result, "Session valid"));
            }
            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error("No session"));
            }
        });
    }
}