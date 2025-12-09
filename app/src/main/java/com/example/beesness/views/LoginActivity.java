package com.example.beesness.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class LoginActivity extends AppCompatActivity {

    TextView signUpLink;
    TextView emailLbl, passwordLbl;
    EditText emailEt, passwordEt;
    Button loginBtn;
    AuthController authController;


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

        authController = new AuthController();

        signUpLink = findViewById(R.id.signUpLink);
        emailLbl = findViewById(R.id.labelLoginEmail);
        passwordLbl = findViewById(R.id.labelLoginPassword);
        emailEt = findViewById(R.id.etLoginEmail);
        passwordEt = findViewById(R.id.etLoginPassword);
        loginBtn = findViewById(R.id.btnSignIn);

        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(v -> handleLogin());
    }

    public void handleLogin(){
        String email, password;
        email = emailEt.getText().toString();
        password = passwordEt.getText().toString();

        authController.login(email, password, result -> {
            switch (result.status) {
                case LOADING:
                    loginBtn.setEnabled(false);
                    loginBtn.setText("Logging In...");
                    break;
                case SUCCESS:
                    loginBtn.setEnabled(false);
                    Toast.makeText(LoginActivity.this,"Login Success!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("USER", result.data);
                    startActivity(intent);
                    finishAffinity(); // Clear back stack so they can't go back to login
                    break;
                case ERROR:
                    loginBtn.setEnabled(true);
                    loginBtn.setText("Login");
                    Toast.makeText(LoginActivity.this, "Error: " + result.message, Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }
}