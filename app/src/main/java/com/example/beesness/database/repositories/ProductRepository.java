package com.example.beesness.database.repositories;
import com.example.beesness.database.interfaces.IProductRepository;
import com.example.beesness.models.Product;
import com.example.beesness.utils.FirestoreCallback;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

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

    @Override
    public void add(Product product, FirestoreCallback<Product> callback) {
        DocumentReference newDocRef = productRef.document();
        product.setId(newDocRef.getId());

        newDocRef.set(product)
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess(product);
                }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void getAll(FirestoreCallback<List<Product>> callback) {
        //not recommended to use this at all :v
        productRef.get().addOnSuccessListener(qs ->
                callback.onSuccess(qs.toObjects(Product.class))
        ).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void getAllByStoreId(String storeId, FirestoreCallback<List<Product>> callback) {
        productRef.whereEqualTo("storeId", storeId).get().addOnSuccessListener(qs ->
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

