package com.example.beesness.database.interfaces;

import java.util.List;

public interface IBaseRepository<T> {
    void add(T item, FirestoreCallback<T> callback);

    void getAll(FirestoreCallback<List<T>> callback);

    void getById(String id, FirestoreCallback<T> callback);

    void update(String id, T item, FirestoreCallback<Void> callback);

    void delete(String id, FirestoreCallback<Void> callback);
}
