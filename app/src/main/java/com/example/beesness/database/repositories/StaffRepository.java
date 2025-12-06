package com.example.beesness.database.repositories;

import com.example.beesness.database.interfaces.IStaffRepository;
import com.example.beesness.models.Staff;
import com.example.beesness.models.Store;
import com.example.beesness.models.User;
import com.example.beesness.utils.FirestoreCallback;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class StaffRepository implements IStaffRepository {
    private static StaffRepository instance;
    private final CollectionReference ref;

    private StaffRepository() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ref = db.collection("staff");
    }

    public static synchronized StaffRepository getInstance(){
        if(instance == null) {
            instance = new StaffRepository();
        }
        return instance;
    }
    @Override
    public void getByUserId(String userId, FirestoreCallback<List<Staff>> callback) {
        ref.whereEqualTo("userId", userId)
                .whereEqualTo("active", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Staff> staffList = queryDocumentSnapshots.toObjects(Staff.class);
                    callback.onSuccess(staffList);
                }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void getByStoreId(String storeId, FirestoreCallback<List<Staff>> callback) {
        ref.whereEqualTo("storeId", storeId)
                .whereEqualTo("active", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Staff> staffList = queryDocumentSnapshots.toObjects(Staff.class);
                    callback.onSuccess(staffList);
                }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void add(Staff staff, FirestoreCallback<Staff> callback) {
        DocumentReference newDocRef = ref.document();
        staff.setId(newDocRef.getId());

        newDocRef.set(staff)
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess(staff);
                })
                .addOnFailureListener(callback::onFailure);
    }

    @Override
    public void getAll(FirestoreCallback<List<Staff>> callback) {
        ref.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Staff> staffList = queryDocumentSnapshots.toObjects(Staff.class);
            callback.onSuccess(staffList);
        }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void getById(String id, FirestoreCallback<Staff> callback) {
        ref.document(id).get().addOnSuccessListener(doc -> {
            if (doc.exists()) callback.onSuccess(doc.toObject(Staff.class));
            else callback.onFailure(new Exception("Staff record not found"));
        }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void update(String id, Staff staff, FirestoreCallback<Void> callback) {
        ref.document(id).set(staff).addOnSuccessListener(aVoid -> {
            callback.onSuccess(null);
        }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void delete(String id, FirestoreCallback<Void> callback) {
        ref.document(id).delete().addOnSuccessListener(aVoid -> {
            callback.onSuccess(null);
        }).addOnFailureListener(callback::onFailure);
    }
}
