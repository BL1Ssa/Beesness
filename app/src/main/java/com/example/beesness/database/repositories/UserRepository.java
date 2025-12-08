package com.example.beesness.database.repositories;

import com.example.beesness.database.interfaces.IUserRepository;
import com.example.beesness.models.User;
import com.example.beesness.utils.FirestoreCallback;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class UserRepository implements IUserRepository {
    private static UserRepository instance;
    private CollectionReference ref;

    private UserRepository(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ref = db.collection("users");
    }

    public static synchronized UserRepository getInstance(){
        if(instance == null){
            instance = new UserRepository();
        }
        return  instance;
    }

    @Override
    public void add(User user, FirestoreCallback<User> callback) {
        DocumentReference newDocRef = ref.document();
        user.setId(newDocRef.getId());

        newDocRef.set(user).addOnSuccessListener(aVoid -> {
            callback.onSuccess(user);
        }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void getAll(FirestoreCallback<List<User>> callback) {
        ref.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<User> userList = queryDocumentSnapshots.toObjects(User.class);
            callback.onSuccess(userList);
        }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void getById(String id, FirestoreCallback<User> callback) {
        ref.document(id).get().addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            callback.onSuccess(user);
        }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void update(String id, User item, FirestoreCallback<Void> callback) {
        ref.document(id).set(item).addOnSuccessListener(aVoid -> {
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
