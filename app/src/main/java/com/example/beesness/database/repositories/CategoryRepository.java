package com.example.beesness.database.repositories;

import com.example.beesness.utils.FirestoreCallback;
import com.example.beesness.database.interfaces.ICategoryRepository;
import com.example.beesness.models.Category;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CategoryRepository implements ICategoryRepository {

    private static CategoryRepository instance;
    private CollectionReference ref;

    private CategoryRepository(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ref = db.collection("categories");
    }

    @Override
    public void add(Category category, FirestoreCallback<Category> callback) {
        ref.add(category)
                .addOnSuccessListener(documentReference -> {
                    category.setId(ref.getId());
                    callback.onSuccess(category);
                }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void getAll(FirestoreCallback<List<Category>> callback) {
        ref.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Category> categoryList = queryDocumentSnapshots.toObjects(Category.class);
            callback.onSuccess(categoryList);
        }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void getById(String id, FirestoreCallback<Category> callback) {
        ref.document(id).get().addOnSuccessListener(documentSnapshot -> {
            Category category = documentSnapshot.toObject(Category.class);
            callback.onSuccess(category);
        }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void update(String id, Category category, FirestoreCallback<Void> callback) {
        ref.document(id).set(category)
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
