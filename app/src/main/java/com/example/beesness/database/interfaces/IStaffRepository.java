package com.example.beesness.database.interfaces;

import com.example.beesness.models.Staff;
import com.example.beesness.models.Store;
import com.example.beesness.models.User;
import com.example.beesness.utils.FirestoreCallback;

import java.util.List;

public interface IStaffRepository extends IBaseRepository<Staff> {
    void getByUserId(String userId, FirestoreCallback<List<Staff>> callback);
    void getByStoreId(String storeId, FirestoreCallback<List<Staff>> callback);
}
