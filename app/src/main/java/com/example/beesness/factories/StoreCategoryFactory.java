package com.example.beesness.factories;

import com.example.beesness.models.StoreCategory;

public class StoreCategoryFactory {
    //for create
    public static StoreCategory create(String name){
        return new StoreCategory(null, name);
    }

    //for update
    public static StoreCategory create(String id, String name){
        return new StoreCategory(id, name);
    }
}
