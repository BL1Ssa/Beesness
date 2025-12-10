package com.example.beesness.factories;

import com.example.beesness.models.Transaction;

public class TransactionFactory {
    public static Transaction create(String type, double totalAmount, String summary){
        return new Transaction(null, type, totalAmount, summary);
    }

    public static Transaction create(String id, String type, double totalAmount, String summary){
        return new Transaction(id, type, totalAmount, summary);
    }
}
