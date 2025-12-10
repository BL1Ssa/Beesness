package com.example.beesness.controller;

import com.example.beesness.R;
import com.example.beesness.database.repositories.ProductRepository;
import com.example.beesness.database.repositories.TransactionRepository;
import com.example.beesness.factories.TransactionFactory;
import com.example.beesness.models.Product;
import com.example.beesness.models.Transaction;
import com.example.beesness.utils.FirestoreCallback;
import com.example.beesness.utils.OperationCallback;
import com.example.beesness.utils.Result;
import com.example.beesness.utils.SessionManager;

import java.util.List;

public class TransactionController {

    private final ProductRepository productRepo;
    private final ProductController productController;
    private final TransactionRepository transactionRepo;

    public TransactionController() {
        this.productRepo = ProductRepository.getInstance();
        this.productController = new ProductController();
        this.transactionRepo = TransactionRepository.getInstance();
    }

    public void processCheckout(List<Product> cartItems, String storeId, double totalInfo, OperationCallback<String> callback) {
        callback.onResult(Result.loading());

        StringBuilder summaryBuilder = new StringBuilder();
        for (Product p : cartItems) {
            summaryBuilder.append(p.getQuantity()).append("x ").append(p.getName()).append(", ");
        }
        String summary = summaryBuilder.toString();

        Transaction transaction = TransactionFactory.create(storeId, "SALE", totalInfo, summary);

        transactionRepo.add(transaction, new FirestoreCallback<Transaction>() {
            @Override
            public void onSuccess(Transaction result) {
                for (Product p : cartItems) {
                    decreaseStock(p.getId(), p.getQuantity());
                }
                callback.onResult(Result.success(transaction.getId(), "Transaction Complete!"));
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    private void decreaseStock(String productId, int quantitySold) {
        productController.getById(productId, result -> {
            if (result.status == Result.Status.SUCCESS && result.data != null) {
                Product p = result.data;

                int newQuantity = p.getQuantity() - quantitySold;
                if (newQuantity < 0) newQuantity = 0; // Safety check

                productController.updateStock(productId, newQuantity, updateResult -> {
                    if (updateResult.status == Result.Status.ERROR) {
                        System.err.println("Failed to update stock: " + updateResult.message);
                    }
                });
            }
        });
    }

    public void getHistory(String storeId, OperationCallback<List<Transaction>> callback) {
        transactionRepo.getAll(storeId, new FirestoreCallback<List<Transaction>>() {
            @Override
            public void onSuccess(List<Transaction> result) {
                if (result.isEmpty()) {
                    callback.onResult(Result.success(result, "No Transactions Found"));
                } else {
                    callback.onResult(Result.success(result));
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }
}