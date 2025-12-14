package com.example.beesness.database.repositories;

import com.example.beesness.utils.FirestoreCallback;
import com.example.beesness.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AuthRepository {

    private static AuthRepository instance;
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    private AuthRepository() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized AuthRepository getInstance() {
        if (instance == null) instance = new AuthRepository();
        return instance;
    }

    public void login(String email, String password, FirestoreCallback<User> callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();

                    fetchUserProfile(uid, callback);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void register(String email, String password, String name, String phonenum, FirestoreCallback<User> callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    String uid = firebaseUser.getUid();
                    User newUser = new User(uid, name, email, phonenum);

                    saveUserProfile(newUser, callback);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void logout() {
        auth.signOut();
    }

    public void getCurrentUser(FirestoreCallback<User> callback) {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) {
            callback.onFailure(new Exception("No user logged in"));
            return;
        }
        fetchUserProfile(firebaseUser.getUid(), callback);
    }

    private void saveUserProfile(User user, FirestoreCallback<User> callback) {
        db.collection("users").document(user.getId())
                .set(user)
                .addOnSuccessListener(aVoid -> callback.onSuccess(user))
                .addOnFailureListener(callback::onFailure);
    }

    private void fetchUserProfile(String uid, FirestoreCallback<User> callback) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        callback.onSuccess(doc.toObject(User.class));
                    } else {
                        callback.onFailure(new Exception("User profile not found"));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }
}