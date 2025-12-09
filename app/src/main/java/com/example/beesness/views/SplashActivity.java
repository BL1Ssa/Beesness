package com.example.beesness.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beesness.R;
import com.example.beesness.controller.AuthController;
import com.example.beesness.models.User;
import com.example.beesness.utils.Result;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 1. Artificial Delay (Optional: just so user sees the logo for 1 sec)
        new Handler().postDelayed(this::checkSession, 1000);
    }

    private void checkSession() {
        AuthController authController = new AuthController();

        authController.checkSession(result -> {
            if (result.status == Result.Status.SUCCESS) {
                // User is logged in -> Go to Dashboard
                startActivity(new Intent(this, MainActivity.class));
            } else {
                // No User -> Go to Login
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish(); // Close Splash so back button doesn't return here
        });
    }
}