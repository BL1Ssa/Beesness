package com.example.beesness.factories;

import com.example.beesness.models.User;

public class UserFactory {
    //for create
    public static User create(String name, String email, String phoneNumber) {
        return new User(null, name, email, phoneNumber);
    }

    //for update
    public static User create(String id, String name, String email, String phoneNumber) {
        return new User(id, name, email, phoneNumber);
    }

}
