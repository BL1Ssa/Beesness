package com.example.beesness.utils;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.example.beesness.models.User;
import com.example.beesness.views.LoginActivity;

public class SessionManager {

    private static final String PREF_NAME = "BeesnessSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_REMEMBER_ME = "isRemembered"; // <--- NEW KEY
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_PHONENUM = "userPhonenum";
    private static final String KEY_CURRENT_STORE_ID = "currentStoreId";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(User user, boolean isRemembered) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putBoolean(KEY_REMEMBER_ME, isRemembered); // <--- SAVE IT
        editor.putString(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_PHONENUM, user.getPhonenum());
        editor.commit();
    }

    public void saveCurrentStore(String storeId) {
        editor.putString(KEY_CURRENT_STORE_ID, storeId);
        editor.commit();
    }

    public User getUserDetail() {
        if (!isLoggedIn()) return null;
        User user = new User();
        user.setId(pref.getString(KEY_USER_ID, null));
        user.setName(pref.getString(KEY_USER_NAME, null));
        user.setEmail(pref.getString(KEY_USER_EMAIL, null));
        user.setPhonenum(pref.getString(KEY_USER_PHONENUM, null));
        return user;
    }

    public String getCurrentStoreId() {
        return pref.getString(KEY_CURRENT_STORE_ID, null);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // NEW HELPER
    public boolean isRemembered() {
        return pref.getBoolean(KEY_REMEMBER_ME, false);
    }

    public void logout() {
        editor.clear();
        editor.commit();
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(context, intent, null);
    }
}