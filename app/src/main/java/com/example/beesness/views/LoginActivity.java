package com.example.beesness.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.beesness.R;
import com.example.beesness.controller.AuthController;
import com.example.beesness.controller.StoreController;
import com.example.beesness.models.Store;
import com.example.beesness.models.User;
import com.example.beesness.utils.OperationCallback;
import com.example.beesness.utils.Result;
import com.example.beesness.utils.SessionManager; // <--- IMPORT THIS

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    TextView signUpLink;
    TextView emailLbl, passwordLbl;
    EditText emailEt, passwordEt;
    Button loginBtn;
    AuthController authController;
    StoreController storeController;

    SessionManager sessionManager;

    CheckBox rememberCb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        authController = new AuthController();
        storeController = new StoreController();

        initViews();

        signUpLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        loginBtn.setOnClickListener(v -> handleLogin());
    }

    private void initViews() {
        signUpLink = findViewById(R.id.signUpLink);
        emailLbl = findViewById(R.id.labelLoginEmail);
        passwordLbl = findViewById(R.id.labelLoginPassword);
        emailEt = findViewById(R.id.etLoginEmail);
        passwordEt = findViewById(R.id.etLoginPassword);
        loginBtn = findViewById(R.id.btnSignIn);
        rememberCb = findViewById(R.id.rememberCb);
    }

    public void handleLogin(){
        String email = emailEt.getText().toString();
        String password = passwordEt.getText().toString();

        authController.login(email, password, result -> {
            switch (result.status) {
                case LOADING:
                    loginBtn.setEnabled(false);
                    loginBtn.setText("Logging In...");
                    break;
                case SUCCESS:
                    User user = result.data;
                    boolean isRemembered = rememberCb.isChecked();
                    if(isRemembered) Toast.makeText(this, "I'll remember this", Toast.LENGTH_SHORT).show();
                    sessionManager.createLoginSession(user, isRemembered);
                    checkStoreAndRedirect(user);
                    break;
                case ERROR:
                    loginBtn.setEnabled(true);
                    loginBtn.setText("Login");
                    Toast.makeText(LoginActivity.this, "Error: " + result.message, Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }

    private void checkStoreAndRedirect(User user) {
        storeController.getByOwnerId(user, new OperationCallback<List<Store>>() {
            @Override
            public void onResult(Result<List<Store>> result) {
                if (result.status == Result.Status.SUCCESS) {
                    List<Store> stores = result.data;

                    if (stores != null && !stores.isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                        // 4. SAVE CURRENT STORE SESSION
                        // Assuming auto-select the first store for now
                        sessionManager.saveCurrentStore(stores.get(0).getId());

                        // 5. CLEAN NAVIGATION (No Extras needed!)
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        // Clear back stack so pressing 'Back' doesn't return to Login
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    } else {
                        // Go to Create Store (CreateStoreActivity will check session for user)
                        Intent intent = new Intent(LoginActivity.this, CreateStoreActivity.class);
                        startActivity(intent);
                    }
                    finish(); // Finish LoginActivity

                } else if(result.status == Result.Status.ERROR){
                    loginBtn.setEnabled(true);
                    loginBtn.setText("Login");
                    Toast.makeText(LoginActivity.this, "Failed to check store status", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}