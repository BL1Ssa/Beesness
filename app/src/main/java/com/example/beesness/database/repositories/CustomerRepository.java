package com.example.beesness.database.repositories;

import com.example.beesness.utils.FirestoreCallback;
import com.example.beesness.database.interfaces.ICustomerRepository;
import com.example.beesness.models.Customer;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CustomerRepository implements ICustomerRepository {
    private static CustomerRepository instance;
    private CollectionReference ref;

    private CustomerRepository(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ref = db.collection("members");
    }

    public static synchronized CustomerRepository getInstance(){
        if(instance == null) {
            instance = new CustomerRepository();
        }
        return instance;
    }
    @Override
    public void add(Customer customer, FirestoreCallback<Customer> callback) {
        ref.add(customer).addOnSuccessListener(documentReference -> {
            customer.setId(ref.getId());
            callback.onSuccess(customer);
        }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void getAll(FirestoreCallback<List<Customer>> callback) {
        ref.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Customer> customerList = queryDocumentSnapshots.toObjects(Customer.class);
            callback.onSuccess(customerList);
        }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void getById(String id, FirestoreCallback<Customer> callback) {
        ref.document(id).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Customer customer = documentSnapshot.toObject(Customer.class);
                    callback.onSuccess(customer);
                }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void update(String id, Customer customer, FirestoreCallback<Void> callback) {
        ref.document(id).set(customer)
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
