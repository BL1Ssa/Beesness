package com.example.beesness.database.interfaces;

import com.example.beesness.models.User;
import com.example.beesness.utils.FirestoreCallback;

import java.util.List;

public interface IUserRepository{
    void add(User item, FirestoreCallback<User> callback);
    void getAll(FirestoreCallback<List<User>> callback);
    void getById(String id, FirestoreCallback<User> callback);
    void update(String id, User item, FirestoreCallback<Void> callback);
    void delete(String id, FirestoreCallback<Void> callback);
}
