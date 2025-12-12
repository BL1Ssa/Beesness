package com.example.beesness.views;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.beesness.R;
import com.example.beesness.models.User;
import com.example.beesness.utils.SessionManager;
import com.example.beesness.views.facade.SetupNavigationFacade;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUsername, tvEmail, tvPhone;
    private Button btnLogout;
    private SessionManager sessionManager;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        setupNavigation();

        sessionManager = new SessionManager(this);
        User user = sessionManager.getUserDetail();

        if (user != null) {
            populateUserData(user);
        } else {
            Toast.makeText(this, "User not found, please login again", Toast.LENGTH_SHORT).show();
        }

        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            finish(); 
        });
    }

    private void initViews() {
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        btnLogout = findViewById(R.id.btnLogout);
        bottomNav = findViewById(R.id.bottom_navigation);
    }

    private void populateUserData(User user) {
        tvUsername.setText(user.getName() != null ? user.getName() : "N/A");
        tvEmail.setText(user.getEmail() != null ? user.getEmail() : "N/A");
        tvPhone.setText(user.getPhonenum() != null ? user.getPhonenum() : "N/A");
    }

    private void setupNavigation() {
        SetupNavigationFacade navFacade = new SetupNavigationFacade(this, bottomNav);
        navFacade.setupNavigation(R.id.nav_profile);
    }
}
