package com.example.beesness.database.repositories;

import com.example.beesness.database.interfaces.IStoreRepository;
import com.example.beesness.models.Store;
import com.example.beesness.utils.FirestoreCallback;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class StoreRepository implements IStoreRepository {

    private static StoreRepository instance;
    private final FirebaseFirestore db;
    private final CollectionReference storeRef;

    // Removed staffRef since the store is now just the owner

    private StoreRepository() {
        db = FirebaseFirestore.getInstance();
        storeRef = db.collection("stores");
    }

    public static synchronized StoreRepository getInstance() {
        if (instance == null) {
            instance = new StoreRepository();
        }
        return instance;
    }

    @Override
    public void createStore(Store store, FirestoreCallback<Store> callback) {
        DocumentReference newStoreDoc = storeRef.document();

        store.setId(newStoreDoc.getId());

        newStoreDoc.set(store)
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess(store);
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e);
                });
    }

    @Override
    public void add(Store store, FirestoreCallback<Store> callback) {
        DocumentReference newDocRef = storeRef.document();
        store.setId(newDocRef.getId());

        newDocRef.set(store)
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess(store);
                })
                .addOnFailureListener(callback::onFailure);
    }

    @Override
    public void getAll(FirestoreCallback<List<Store>> callback) {
        storeRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Store> storeList = queryDocumentSnapshots.toObjects(Store.class);
            callback.onSuccess(storeList);
        }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void getById(String id, FirestoreCallback<Store> callback) {
        storeRef.document(id).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        callback.onSuccess(doc.toObject(Store.class));
                    } else {
                        callback.onFailure(new Exception("Store not found"));
                    }
                }).addOnFailureListener(callback::onFailure);
    }

    public void getByOwnerId(String ownerId, FirestoreCallback<List<Store>> callback){
        storeRef.whereEqualTo("ownerId", ownerId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Store> storeList = queryDocumentSnapshots.toObjects(Store.class);
                    callback.onSuccess(storeList);
                }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void update(String id, Store store, FirestoreCallback<Void> callback) {
        store.setId(id);
        storeRef.document(id).set(store)
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess(null);
                }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void delete(String id, FirestoreCallback<Void> callback) {
        storeRef.document(id).delete()
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess(null);
                }).addOnFailureListener(callback::onFailure);
    }
}