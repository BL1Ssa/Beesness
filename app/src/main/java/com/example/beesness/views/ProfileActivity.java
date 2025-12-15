package com.example.beesness.views;

import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beesness.R;
import com.example.beesness.controller.UserController;
import com.example.beesness.factories.UserFactory;
import com.example.beesness.models.Product;
import com.example.beesness.models.User;
import com.example.beesness.utils.Result;
import com.example.beesness.utils.SessionManager;
import com.example.beesness.views.facade.SetupNavigationFacade;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUsername, tvEmail, tvPhone;
    private Button btnLogout, btnEdit;
    private SessionManager sessionManager;
    private User currentUser;
    private BottomNavigationView bottomNav;
    private UserController userController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        setupNavigation();

        sessionManager = new SessionManager(this);
        currentUser = sessionManager.getUserDetail();
        userController = new UserController();

        if (currentUser != null) {
            populateUserData(currentUser);
        } else {
            Toast.makeText(this, "User not found, please login again", Toast.LENGTH_SHORT).show();
        }




    }

    private void initViews() {
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        btnLogout = findViewById(R.id.btnLogout);
        btnEdit = findViewById(R.id.btnEditProfile);
        bottomNav = findViewById(R.id.bottom_navigation);

        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            finish();
        });

        btnEdit.setOnClickListener(v -> {
            showEditProfileDialog(currentUser);
        });

    }

    private void showEditProfileDialog(User user) {
        int p = 40;

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(p, p, p, p);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 30);

        TextView nameLbl = new TextView(this);
        nameLbl.setText("Name");
        layout.addView(nameLbl);

        EditText nameInput = new EditText(this);
        nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        nameInput.setHint(R.string.exampleName);
        nameInput.setPadding(p, p, p, p);
        nameInput.setLayoutParams(params);
        layout.addView(nameInput);

        TextView emailLbl = new TextView(this);
        emailLbl.setText("Email (Cannot be changed)");
        layout.addView(emailLbl);

        EditText emailInput = new EditText(this);
        emailInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailInput.setHint(R.string.exampleEmail);
        emailInput.setPadding(p, p, p, p);
        emailInput.setLayoutParams(params);
        emailInput.setEnabled(false);
        layout.addView(emailInput);

        TextView phoneLbl = new TextView(this);
        phoneLbl.setText("Phone Number");
        layout.addView(phoneLbl);

        EditText phoneInput = new EditText(this);
        phoneInput.setInputType(InputType.TYPE_CLASS_PHONE);
        phoneInput.setHint(R.string.examplePhonenum);
        phoneInput.setPadding(p, p, p, p);
        phoneInput.setLayoutParams(params);
        layout.addView(phoneInput);

        nameInput.setText(user.getName());
        emailInput.setText(user.getEmail());
        phoneInput.setText(user.getPhonenum());

        new AlertDialog.Builder(this)
                .setTitle("Edit Profile")
                .setView(layout)
                .setPositiveButton("Save", ((dialog, which) -> {
                    String name = nameInput.getText().toString();
                    String email = emailInput.getText().toString();
                    String phone = phoneInput.getText().toString();

                    userController.update(user.getId(), name, email, phone, result -> {
                        if (result.status == Result.Status.SUCCESS) {
                            populateUserData(UserFactory.create(user.getId(), name, email, phone));
                            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        }
                        if(result.status == Result.Status.ERROR){
                            Toast.makeText(ProfileActivity.this, "Error: " + result.message, Toast.LENGTH_SHORT).show();
                        }
                        if(result.status == Result.Status.LOADING){
                            Toast.makeText(ProfileActivity.this, "Updating...", Toast.LENGTH_SHORT).show();
                        }
                    });
                }))
                .setNegativeButton("Cancel", null)
                .show();
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
