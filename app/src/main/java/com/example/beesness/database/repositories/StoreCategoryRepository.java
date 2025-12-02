package com.example.beesness.database.repositories;

import com.example.beesness.models.StoreCategory;
import com.example.beesness.utils.FirestoreCallback;
import com.example.beesness.database.interfaces.IStoreCategoryRepository;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class StoreCategoryRepository implements IStoreCategoryRepository {

    private static StoreCategoryRepository instance;
    private CollectionReference ref;

    private StoreCategoryRepository(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ref = db.collection("categories");
    }

    @Override
    public void add(StoreCategory storeCategory, FirestoreCallback<StoreCategory> callback) {
        ref.add(storeCategory)
                .addOnSuccessListener(documentReference -> {
                    storeCategory.setId(ref.getId());
                    callback.onSuccess(storeCategory);
                }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void getAll(FirestoreCallback<List<StoreCategory>> callback) {
        ref.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<StoreCategory> storeCategoryList = queryDocumentSnapshots.toObjects(StoreCategory.class);
            callback.onSuccess(storeCategoryList);
        }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void getById(String id, FirestoreCallback<StoreCategory> callback) {
        ref.document(id).get().addOnSuccessListener(documentSnapshot -> {
            StoreCategory storeCategory = documentSnapshot.toObject(StoreCategory.class);
            callback.onSuccess(storeCategory);
        }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void update(String id, StoreCategory storeCategory, FirestoreCallback<Void> callback) {
        ref.document(id).set(storeCategory)
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess(null);
                }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void delete(String id, FirestoreCallback<Void> callback) {
        ref.document(id).delete()
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess(null);
                }).addOnFailureListener(callback::onFailure);
    }
}
