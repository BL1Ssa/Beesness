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
                processStockUpdates(0, cartItems, false, () -> { // false = decrease
                    callback.onResult(Result.success(transaction.getId(), "Transaction Complete!"));
                });
            }
            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    public void processProcurement(List<Product> procurementItems, String storeId, double totalCost, OperationCallback<String> callback) {
        callback.onResult(Result.loading());

        StringBuilder summaryBuilder = new StringBuilder();
        for (Product p : procurementItems) {
            summaryBuilder.append(p.getQuantity()).append("x ").append(p.getName()).append(", ");
        }
        String summary = summaryBuilder.toString();

        Transaction transaction = TransactionFactory.create(storeId, "PROCUREMENT", totalCost, summary);

        transactionRepo.add(transaction, new FirestoreCallback<Transaction>() {
            @Override
            public void onSuccess(Transaction result) {
                processStockUpdates(0, procurementItems, true, () -> {
                    callback.onResult(Result.success(transaction.getId(), "Procurement Complete!"));
                });
            }

            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.error(e.getMessage()));
            }
        });
    }

    private void processStockUpdates(int index, List<Product> items, boolean isIncrease, Runnable onComplete) {
        if (index >= items.size()) {
            onComplete.run();
            return;
        }

        Product p = items.get(index);
        //lmao crazy recursive
        updateStockQty(p.getId(), p.getQuantity(), isIncrease, () -> {
            processStockUpdates(index + 1, items, isIncrease, onComplete);
        });
    }

    private void updateStockQty(String productId, int quantityChange, boolean isIncrease, Runnable onDone) {
        productController.getById(productId, result -> {

            if (result.status == Result.Status.LOADING) return;

            if (result.status == Result.Status.SUCCESS && result.data != null) {
                Product p = result.data;

                int newQuantity;
                if (isIncrease) {
                    newQuantity = p.getQuantity() + quantityChange;
                } else {
                    newQuantity = p.getQuantity() - quantityChange;
                    if (newQuantity < 0) newQuantity = 0;
                }

                productController.updateStock(productId, newQuantity, updateResult -> {
                    onDone.run();
                });
            } else {
                onDone.run();
            }
        });
    }

    public void getHistory(String storeId, OperationCallback<List<Transaction>> callback) {
        callback.onResult(Result.loading());
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

    public void getSalesHistory(String storeId, OperationCallback<List<Transaction>> callback){
        callback.onResult(Result.loading());
        transactionRepo.getSalesHistory(storeId, new FirestoreCallback<List<Transaction>>() {
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

    public void getProcurementHistory(String storeId, OperationCallback<List<Transaction>> callback){
        callback.onResult(Result.loading());
        transactionRepo.getProcurementHistory(storeId, new FirestoreCallback<List<Transaction>>() {
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

    public void recordInitialExpense(String storeId, String productName, int quantity, double totalCost, OperationCallback<String> callback) {
        String summary = quantity + "x " + productName + " (Initial Stock)";
        // Create a PROCUREMENT transaction
        Transaction transaction = TransactionFactory.create(storeId, "PROCUREMENT", totalCost, summary);

        transactionRepo.add(transaction, new FirestoreCallback<Transaction>() {
            @Override
            public void onSuccess(Transaction result) {
                callback.onResult(Result.success(result.getId(), "Expense Recorded"));
            }
            @Override
            public void onFailure(Exception e) {
                // Even if this fails, the product was created, so we might just log it or warn the user
                callback.onResult(Result.error("Failed to record expense: " + e.getMessage()));
            }
        });
    }

}