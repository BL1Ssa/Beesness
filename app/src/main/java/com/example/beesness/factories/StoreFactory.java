package com.example.beesness.factories;

import com.example.beesness.models.Store;
import com.example.beesness.models.StoreCategory;

public class StoreFactory {

    //for adding
    public static Store create(String name, String address, String phone, String ownerId, String currency, StoreCategory category) {

        String catId = (category != null) ? category.getId() : "GEN";

        return new Store(null, name, address, phone, ownerId, currency, catId);
    }

    //for updating
    public static Store create(String id, String name, String address, String phone, String ownerId, String currency, StoreCategory category) {


        return new Store(id, name, address, phone, ownerId, currency, category.getId());
    }
}