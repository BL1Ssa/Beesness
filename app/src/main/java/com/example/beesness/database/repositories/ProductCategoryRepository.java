package com.example.beesness.database.repositories;

import com.example.beesness.database.interfaces.IProductCategoryRepository;
import com.example.beesness.models.ProductCategory;
import com.example.beesness.models.StoreCategory;
import com.example.beesness.utils.FirestoreCallback;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ProductCategoryRepository implements IProductCategoryRepository {
    private static ProductCategoryRepository instance;
    private final CollectionReference ref;

    private ProductCategoryRepository(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ref = db.collection("productcategories");
    }

    public static synchronized ProductCategoryRepository getInstance(){
        if(instance == null){
            instance = new ProductCategoryRepository();
        }
        return instance;
    }

    @Override
    public void add(ProductCategory productCategory, FirestoreCallback<ProductCategory> callback) {
        DocumentReference newDocRef = ref.document();
        productCategory.setId(newDocRef.getId());

        newDocRef.set(productCategory)
                .addOnSuccessListener(aVoid ->{
                    callback.onSuccess(productCategory);
                }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void getAll(FirestoreCallback<List<ProductCategory>> callback) {
        ref.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<ProductCategory> productCategoryList = queryDocumentSnapshots.toObjects(ProductCategory.class);
            callback.onSuccess(productCategoryList);
        }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void getById(String id, FirestoreCallback<ProductCategory> callback) {
        ref.document(id).get()
                .addOnSuccessListener(documentSnapshot -> {
                    ProductCategory productCategory = documentSnapshot.toObject(ProductCategory.class);
                    callback.onSuccess(productCategory);
                }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void update(String id, ProductCategory item, FirestoreCallback<Void> callback) {
        ref.document(id).set(item)
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess(null);
                }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void delete(String id, FirestoreCallback<Void> callback) {
        ref.document(id).delete()
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess(null);
                }).addOnFailureListener(callback::onFailure);
    }
}
