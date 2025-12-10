package com.example.beesness.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.beesness.R;
import com.example.beesness.controller.AuthController;
import com.example.beesness.controller.StoreController;
import com.example.beesness.models.Store;
import com.example.beesness.models.User;
import com.example.beesness.utils.Result;
import com.example.beesness.utils.OperationCallback;
import com.example.beesness.utils.SessionManager;

import java.util.List;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private AuthController authController;
    private StoreController storeController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        authController = new AuthController();
        storeController = new StoreController();

        new Handler().postDelayed(this::checkSessionAndStore, 1000);
    }

    private void checkSessionAndStore() {
        SessionManager session = new SessionManager(this);

        if (session.isLoggedIn()) {
            if (session.isRemembered()) {
                navigateToMain();
            } else {
                session.logout();
                navigateToLogin();
            }
        } else {
            navigateToLogin();
        }
    }

    private void checkStoreState(User user) {
        storeController.getByOwnerId(user, new OperationCallback<List<Store>>() {
            @Override
            public void onResult(Result<List<Store>> result) {
                if (result.status == Result.Status.SUCCESS) {
                    List<Store> stores = result.data;

                    if (stores != null && !stores.isEmpty()) {
                        navigateToMain();
                    } else {
                        navigateToCreateStore();
                    }
                } else {
                    Toast.makeText(SplashActivity.this, "Error syncing data. Please login again.", Toast.LENGTH_SHORT).show();
                    navigateToLogin();
                }
            }
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToCreateStore() {
        Intent intent = new Intent(SplashActivity.this, CreateStoreActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}