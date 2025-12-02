package com.example.beesness.factories;

import com.example.beesness.models.Store;
import com.example.beesness.models.StoreCategory;

public class StoreFactory {

    public static Store create(String name, String address, String phone, String ownerId, String currency, StoreCategory category) {

        // setting category id for the storeCategory
        String catId = (category != null) ? category.getId() : "GEN";

        // RN we'll just use Rp. as our product is only for Indonesia use only
        return new Store(null, name, address, phone, ownerId, currency, catId);
    }
}