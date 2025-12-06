package com.example.beesness.factories;

import com.example.beesness.models.Staff;
import com.example.beesness.models.User;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StaffFactory {

    private static String getToday() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
    }

    // SPECIAL: System uses this when creating a Store.
    // "OWNER" is hardcoded here because the system enforces this rule.
    public static Staff createOwner(User user) {
        return new Staff(null, user.getId(), null, "OWNER", user.getEmail(), getToday());
    }

    public static Staff create(String userId, String userEmail, String storeId, String role) {
        String cleanRole = role.toUpperCase().trim();

        return new Staff(null, userId, storeId, cleanRole, userEmail, getToday());
    }

    //for updating
    public static Staff create(String id, String userId, String userEmail, String storeId, String role) {
        String cleanRole = role.toUpperCase().trim();

        return new Staff(id, userId, storeId, cleanRole, userEmail, getToday());
    }
}