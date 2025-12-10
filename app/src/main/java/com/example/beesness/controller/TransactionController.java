package com.example.beesness.controller;

import com.example.beesness.database.repositories.ProductRepository;
import com.example.beesness.database.repositories.TransactionRepository;
import com.example.beesness.models.Product;
import com.example.beesness.models.Transaction;
import com.example.beesness.utils.FirestoreCallback;
import com.example.beesness.utils.OperationCallback;
import com.example.beesness.utils.Result;

import java.util.List;

public class TransactionController {

    private final ProductRepository productRepo;
    private final ProductController productController;
    private final TransactionRepository transactionRepo; // <--- Use Repo instead of DB

    public TransactionController() {
        this.productRepo = ProductRepository.getInstance();
        this.productController = new ProductController();
        this.transactionRepo = TransactionRepository.getInstance(); // <--- Init Repo
    }

    public void processCheckout(List<Product> cartItems, double totalInfo, OperationCallback<String> callback) {
        callback.onResult(Result.loading());

        // 1. Prepare Summary String
        StringBuilder summaryBuilder = new StringBuilder();
        for (Product p : cartItems) {
            summaryBuilder.append(p.getQuantity()).append("x ").append(p.getName()).append(", ");
        }
        String summary = summaryBuilder.toString();

        // 2. Create Transaction Object (Pass null for ID, Repo will generate it)
        Transaction transaction = new Transaction(null, "SALE", totalInfo, summary);

        // 3. Use Repository to Save
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

                // 2. Calculate
                int newQuantity = p.getQuantity() - quantitySold;
                if (newQuantity < 0) newQuantity = 0; // Safety check

                // 3. Command ProductController to update
                productController.updateStock(productId, newQuantity, updateResult -> {
                    if (updateResult.status == Result.Status.ERROR) {
                        System.err.println("Failed to update stock: " + updateResult.message);
                    }
                });
            }
        });
    }
}