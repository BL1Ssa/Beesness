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

    // GENERIC: For use of any kinds of staffs. However user do need to input the roles here which might be sad lmao
    public static Staff create(String userId, String userEmail, String storeId, String role) {
        // We might want to uppercase the role for consistency
        String cleanRole = role.toUpperCase().trim();

        return new Staff(null, userId, storeId, cleanRole, userEmail, getToday());
    }
}