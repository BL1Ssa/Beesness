package com.example.beesness.database.repositories;
import com.example.beesness.database.interfaces.IProductRepository;
import com.example.beesness.models.Product;
import com.example.beesness.utils.CounterData;
import com.example.beesness.utils.FirestoreCallback;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Locale;

public class ProductRepository implements IProductRepository {

    private static ProductRepository instance;
    private final FirebaseFirestore db;
    private final CollectionReference productRef;

    private ProductRepository() {
        db = FirebaseFirestore.getInstance();
        productRef = db.collection("products");
    }

    public static synchronized ProductRepository getInstance() {
        if (instance == null) {
            instance = new ProductRepository();
        }
        return instance;
    }

    //custom add with id assignment generator
    @Override
    public void add(Product product, String categoryCode, FirestoreCallback<Product> callback) {

        String prefix = categoryCode.trim().toUpperCase(Locale.ROOT);

        DocumentReference counterRef = db.collection("counters").document(prefix);

        // 3. Run Atomic Transaction
        db.runTransaction(transaction -> {

            DocumentSnapshot snapshot = transaction.get(counterRef);
            long nextCount = 1;

            if (snapshot.exists()) {
                Long current = snapshot.getLong("count");
                if (current != null) {
                    nextCount = current + 1;
                }
            }

            String customId = prefix + String.format(Locale.US, "%03d", nextCount);

            product.setId(customId);

            transaction.set(counterRef, new CounterData(nextCount));
            transaction.set(productRef.document(customId), product);

            return product;

        }).addOnSuccessListener(resultProduct -> {
            callback.onSuccess(resultProduct);
        }).addOnFailureListener(e -> {
            callback.onFailure(e);
        });
    }

    @Override
    public void add(Product item, FirestoreCallback<Product> callback) {
        callback.onFailure(new Exception("DO NOT USE THIS. Use add(product, categoryCode) instead."));
        productRef.add(item).addOnSuccessListener(doc -> {
            item.setId(doc.getId());
            callback.onSuccess(item);
        }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void getAll(FirestoreCallback<List<Product>> callback) {
        productRef.get().addOnSuccessListener(qs ->
                callback.onSuccess(qs.toObjects(Product.class))
        ).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void getById(String id, FirestoreCallback<Product> callback) {
        productRef.document(id).get().addOnSuccessListener(doc -> {
            if (doc.exists()) callback.onSuccess(doc.toObject(Product.class));
            else callback.onFailure(new Exception("Product not found"));
        }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void update(String id, Product item, FirestoreCallback<Void> callback) {
        // Ensure ID is set correctly
        item.setId(id);
        productRef.document(id).set(item)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }

    @Override
    public void delete(String id, FirestoreCallback<Void> callback) {
        productRef.document(id).delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }
}

