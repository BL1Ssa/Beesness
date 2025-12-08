package com.example.beesness.database.interfaces;

import com.example.beesness.models.Staff;
import com.example.beesness.models.Store;
import com.example.beesness.utils.FirestoreCallback;

public interface IStoreRepository extends IBaseRepository<Store>{
    //finally something that doesn't add Speculative Generality code smell
    void createStore(Store store, Staff initialStaff, FirestoreCallback<Store> callback);
}

