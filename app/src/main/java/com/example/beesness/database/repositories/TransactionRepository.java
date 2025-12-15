package com.example.beesness.database.repositories;

import com.example.beesness.database.interfaces.ITransactionRepository;
import com.example.beesness.models.Transaction;
import com.example.beesness.utils.FirestoreCallback;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;

public class TransactionRepository implements ITransactionRepository {

    private static TransactionRepository instance;
    private final CollectionReference ref;

    private TransactionRepository() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ref = db.collection("transactions");
    }

    public static synchronized TransactionRepository getInstance() {
        if (instance == null) {
            instance = new TransactionRepository();
        }
        return instance;
    }

    @Override
    public void add(Transaction transaction, FirestoreCallback<Transaction> callback) {
        DocumentReference newDocRef = ref.document();

        transaction.setId(newDocRef.getId());
        newDocRef.set(transaction)
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess(transaction);
                }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void getAll(String storeId, FirestoreCallback<List<Transaction>> callback) {
        ref.whereEqualTo("storeId", storeId)
                .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Transaction> transactionList = queryDocumentSnapshots.toObjects(Transaction.class);
                    callback.onSuccess(transactionList);
                }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void getById(String id, FirestoreCallback<Transaction> callback) {
        ref.document(id).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Transaction transaction = documentSnapshot.toObject(Transaction.class);
                    callback.onSuccess(transaction);
                }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void delete(String id, FirestoreCallback<Void> callback) {
        ref.document(id).delete()
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess(null);
                }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void getSalesHistory(String storeId, FirestoreCallback<List<Transaction>> callback) {
        ref.whereEqualTo("storeId", storeId)
                .whereEqualTo("type", "SALE")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Transaction> transactionList = queryDocumentSnapshots.toObjects(Transaction.class);
                    callback.onSuccess(transactionList);
                })
                .addOnFailureListener(callback::onFailure);
    }

    @Override
    public void getProcurementHistory(String storeId, FirestoreCallback<List<Transaction>> callback) {
        ref.whereEqualTo("storeId", storeId)
                .whereEqualTo("type", "PROCUREMENT")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Transaction> transactionList = queryDocumentSnapshots.toObjects(Transaction.class);
                    callback.onSuccess(transactionList);
                })
                .addOnFailureListener(callback::onFailure);
    }
}