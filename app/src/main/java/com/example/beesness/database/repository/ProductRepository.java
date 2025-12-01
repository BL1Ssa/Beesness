package com.example.beesness.database.repository;

import com.example.beesness.database.interfaces.FirestoreCallback;
import com.example.beesness.database.interfaces.IProductRepository;
import com.example.beesness.models.Product;
import com.example.beesness.models.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ProductRepository implements IProductRepository {
    private static ProductRepository instance;
    private final CollectionReference ref;

    private ProductRepository() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ref = db.collection("products");
    }

    public static synchronized ProductRepository getInstance(){
        if(instance == null){
            instance = new ProductRepository();
        }
        return instance;
    }

    @Override
    public void add(Product product, FirestoreCallback<Product> callback) {
        ref.add(product).addOnSuccessListener(documentReference -> {
            product.setId(documentReference.getId());
            callback.onSuccess(product);
        }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void getAll(FirestoreCallback<List<Product>> callback) {
        ref.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Product> productList = queryDocumentSnapshots.toObjects(Product.class);
            callback.onSuccess(productList);
        }).addOnFailureListener(e -> {
            callback.onFailure(e);
        });
    }

    @Override
    public void getById(String id, FirestoreCallback<Product> callback) {
        ref.document(id).get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()) {
                Product product = documentSnapshot.toObject(Product.class);
                callback.onSuccess(product);
            }else {
                callback.onFailure(new Exception("Product not found with ID: " + id));
            }
        }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void update(String id, Product product, FirestoreCallback<Void> callback) {
        ref.document(id).set(product)
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
