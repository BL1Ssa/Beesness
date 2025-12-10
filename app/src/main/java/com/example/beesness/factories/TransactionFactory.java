package com.example.beesness.factories;

import com.example.beesness.models.Transaction;

public class TransactionFactory {
    public static Transaction create(String storeId, String type, double totalAmount, String summary){
        return new Transaction(null, storeId, type, totalAmount, summary);
    }

    public static Transaction create(String id, String storeId, String type, double totalAmount, String summary){
        return new Transaction(id, storeId, type, totalAmount, summary);
    }
}
