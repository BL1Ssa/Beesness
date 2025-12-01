package com.example.beesness.database.repositories;

import com.example.beesness.database.interfaces.FirestoreCallback;
import com.example.beesness.database.interfaces.IUserRepository;
import com.example.beesness.models.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class UserRepository implements IUserRepository {
    private static UserRepository instance;
    private final CollectionReference ref;

    private UserRepository() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ref = db.collection("users");
    }

    //dibuat singleton untuk hemat memori sebesar mungkin
    public static synchronized UserRepository getInstance(){
        if(instance == null){
            instance = new UserRepository();
        }
        return instance;
    }

    @Override
    public void add(User user, FirestoreCallback<User> callback){
        ref.add(user).addOnSuccessListener(documentReference -> {
            user.setId(documentReference.getId());
            callback.onSuccess(user);
        }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void getAll(FirestoreCallback<List<User>> callback){
        ref.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<User> userList = queryDocumentSnapshots.toObjects(User.class);
            callback.onSuccess(userList);
        }).addOnFailureListener(e -> {
            callback.onFailure(e);
        });
    }

    @Override
    public void getById(String id, FirestoreCallback<User> callback) {
        ref.document(id).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        callback.onSuccess(user);
                    } else {
                        callback.onFailure(new Exception("User not found with ID: " + id));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    @Override
    public void update(String id, User user, FirestoreCallback<Void> callback) {
        ref.document(id).set(user)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }

    @Override
    public void delete(String id, FirestoreCallback<Void> callback) {
        ref.document(id).delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }
}
