package com.example.beesness.database.interfaces;

import com.example.beesness.models.Store;
import com.example.beesness.utils.FirestoreCallback;

public interface IStoreRepository extends IBaseRepository<Store>{
    // Refactored: Removed 'Staff' parameter and removed the Transaction.
    // Since we are only writing to 'stores' now, a transaction is unnecessary overhead.
    void createStore(Store store, FirestoreCallback<Store> callback);
}

