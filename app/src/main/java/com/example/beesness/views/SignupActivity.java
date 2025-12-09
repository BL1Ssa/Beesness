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
import com.example.beesness.models.User;
import com.example.beesness.utils.OperationCallback;
import com.example.beesness.utils.Result;

public class SignupActivity extends AppCompatActivity {

    TextView loginLink;
    TextView nameLbl, emailLbl, phonenumLbl, passwordLbl, confirmPasswordLbl;
    EditText nameEt, emailEt, phonenumEt, passwordEt, confirmPasswordEt;
    Button signupBtn;
    AuthController authController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        authController = new AuthController();

        loginLink = findViewById(R.id.loginLink);

        nameLbl = findViewById(R.id.labelSignUpName);
        emailLbl = findViewById(R.id.labelSignUpEmail);
        phonenumLbl = findViewById(R.id.labelSignUpPhonenum);
        passwordLbl = findViewById(R.id.labelSignUpPassword);
        confirmPasswordLbl = findViewById(R.id.labelSignUpConfirmPassword);

        nameEt = findViewById(R.id.etSignUpName);
        emailEt = findViewById(R.id.etSignUpEmail);
        phonenumEt = findViewById(R.id.etSignUpPhonenum);
        passwordEt = findViewById(R.id.etSignUpPassword);
        confirmPasswordEt = findViewById(R.id.etSignUpConfirmPassword);

        signupBtn = findViewById(R.id.btnSignUp);

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        signupBtn.setOnClickListener(v -> handleSignUp());
    }

    public void handleSignUp(){
        String name, email, phonenum, password, confirmPassword;
        name = nameEt.getText().toString();
        email = emailEt.getText().toString();
        phonenum = phonenumEt.getText().toString();
        password = passwordEt.getText().toString();
        confirmPassword = confirmPasswordEt.getText().toString();

        authController.register(name, email, phonenum, password, confirmPassword, result -> {
            switch (result.status) {
                case LOADING:
                    signupBtn.setEnabled(false);
                    signupBtn.setText("Creating Account...");
                    break;

                case SUCCESS:
                    signupBtn.setEnabled(true);
                    Toast.makeText(SignupActivity.this,"Account Created!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignupActivity.this, CreateStoreActivity.class);
                    // Pass the User object so CreateStore knows who the owner is
                    intent.putExtra("USER", result.data);
                    startActivity(intent);
                    finishAffinity(); // Clear back stack so they can't go back to signup
                    break;

                case ERROR:
                    signupBtn.setEnabled(true);
                    signupBtn.setText("Sign Up");
                    Toast.makeText(SignupActivity.this, "Error: " + result.message, Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }
}