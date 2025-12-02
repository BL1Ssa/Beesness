package com.example.beesness.database.repositories;

import com.example.beesness.database.interfaces.IStoreRepository;
import com.example.beesness.models.Staff;
import com.example.beesness.models.Store;
import com.example.beesness.utils.FirestoreCallback;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class StoreRepository implements IStoreRepository {

    private static StoreRepository instance;
    private final FirebaseFirestore db;
    //well sucks that it adds another code smell here lmao
    private final CollectionReference storeRef;
    private final CollectionReference staffRef;

    private StoreRepository() {
        db = FirebaseFirestore.getInstance();
        storeRef = db.collection("stores");
        staffRef = db.collection("staff");
    }

    public static synchronized StoreRepository getInstance() {
        if (instance == null) {
            instance = new StoreRepository();
        }
        return instance;
    }

    @Override
    public void createStore(Store store, Staff initialStaff, FirestoreCallback<Store> callback) {
        DocumentReference newStoreDoc = storeRef.document();
        DocumentReference newStaffDoc = staffRef.document();

        //Set the IDs into the objects
        store.setId(newStoreDoc.getId());

        initialStaff.setId(newStaffDoc.getId());
        initialStaff.setStoreId(newStoreDoc.getId()); // <--- LINKING HAPPENS HERE

        //Run Atomic Transaction
        db.runTransaction(transaction -> {
            //Write to 'stores' collection
            transaction.set(newStoreDoc, store);

            //Write to 'staff' collection
            transaction.set(newStaffDoc, initialStaff);

            return store; //Return success object

        }).addOnSuccessListener(createdStore -> {
            callback.onSuccess(createdStore);
        }).addOnFailureListener(e -> {
            callback.onFailure(e);
        });
    }

    @Override
    public void add(Store store, FirestoreCallback<Store> callback) {
        storeRef.add(store).addOnSuccessListener(documentReference -> {
            store.setId(documentReference.getId());
            callback.onSuccess(store);
        }).addOnFailureListener(callback::onFailure);
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

    @Override
    public void update(String id, Store store, FirestoreCallback<Void> callback) {
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
