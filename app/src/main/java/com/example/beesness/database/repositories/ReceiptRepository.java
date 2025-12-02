package com.example.beesness.database.repositories;

import com.example.beesness.utils.FirestoreCallback;
import com.example.beesness.database.interfaces.IReceiptRepository;
import com.example.beesness.models.Receipt;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ReceiptRepository implements IReceiptRepository {
    private static ReceiptRepository instance;
    private final CollectionReference ref;

    private ReceiptRepository(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ref = db.collection("receipts");
    }
    public static synchronized  ReceiptRepository getInstance(){
        if(instance == null){
            instance = new ReceiptRepository();
        }
        return instance;
    }

    @Override
    public void add(Receipt receipt, FirestoreCallback <Receipt> callback) {
        ref.add(receipt).addOnSuccessListener(documentReference -> {
            receipt.setId(ref.getId());
            callback.onSuccess(receipt);
        }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void getAll(FirestoreCallback<List<Receipt>> callback) {
        ref.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Receipt> receiptList = queryDocumentSnapshots.toObjects(Receipt.class);
            callback.onSuccess(receiptList);
        }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void getById(String id, FirestoreCallback<Receipt> callback) {
        ref.document(id).get().addOnSuccessListener(documentSnapshot -> {
            Receipt receipt = documentSnapshot.toObject(Receipt.class);
            callback.onSuccess(receipt);
        }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void update(String id, Receipt item, FirestoreCallback<Void> callback) {
        ref.document(id).set(item)
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
