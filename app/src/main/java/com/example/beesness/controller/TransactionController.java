package com.example.beesness.controller;

import com.example.beesness.database.repositories.ProductRepository;
import com.example.beesness.database.repositories.TransactionRepository;
import com.example.beesness.factories.TransactionFactory;
import com.example.beesness.models.Product;
import com.example.beesness.models.Transaction;
import com.example.beesness.utils.FirestoreCallback;
import com.example.beesness.utils.OperationCallback;
import com.example.beesness.utils.Result;

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
                // FIXED: We now wait for stock updates to finish before calling success
                processStockUpdates(0, cartItems, () -> {
                    callback.onResult(Result.success(transaction.getId(), "Transaction Complete!"));
                });
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    // Helper to process updates sequentially
    private void processStockUpdates(int index, List<Product> items, Runnable onComplete) {
        if (index >= items.size()) {
            onComplete.run(); // All items processed
            return;
        }

        Product itemToUpdate = items.get(index);

        // Update this item, then recursively call for the next one
        decreaseStockWithCallback(itemToUpdate.getId(), itemToUpdate.getQuantity(), () -> {
            processStockUpdates(index + 1, items, onComplete);
        });
    }

    private void decreaseStockWithCallback(String productId, int quantitySold, Runnable onStepComplete) {
        productController.getById(productId, result -> {
            if (result.status == Result.Status.SUCCESS && result.data != null) {
                Product p = result.data;

                int newQuantity = p.getQuantity() - quantitySold;
                if (newQuantity < 0) newQuantity = 0;

                productController.updateStock(productId, newQuantity, updateResult -> {
                    // Proceed to next item regardless of individual success/fail to prevent hanging
                    onStepComplete.run();
                });
            } else {
                // Product not found, skip
                onStepComplete.run();
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