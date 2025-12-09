package com.example.beesness.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.beesness.R;
import com.example.beesness.controller.StoreCategoryController; // Add this
import com.example.beesness.controller.StoreController;
import com.example.beesness.models.StoreCategory;
import com.example.beesness.models.User;
import com.example.beesness.utils.OperationCallback;
import com.example.beesness.utils.Result;

import java.util.List;

public class CreateStoreActivity extends AppCompatActivity {

    private EditText etName, etAddress, etPhone;
    private Spinner spinnerCategory;
    private Button btnCreate;
    private ProgressBar progressBar;

    private StoreController storeController;
    private StoreCategoryController categoryController; // Add this
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_store);

        // 1. Initialize Views
        etName = findViewById(R.id.etStoreName);
        etAddress = findViewById(R.id.etStoreAddress);
        etPhone = findViewById(R.id.etStorePhone);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnCreate = findViewById(R.id.btnCreateStore);
        progressBar = findViewById(R.id.progressBar);

        // 2. Initialize Controllers
        storeController = new StoreController();
        categoryController = new StoreCategoryController(); // Initialize this

        // 3. Get User from Intent
        // Ensure your LoginActivity passes this object!
        currentUser = (User) getIntent().getSerializableExtra("USER");

        if (currentUser == null) {
            Toast.makeText(this, "Session Error: Please Login Again", Toast.LENGTH_LONG).show();
            finish(); // Close activity
            return;
        }

        // 4. Load Categories
        loadCategories();

        // 5. Handle Click
        btnCreate.setOnClickListener(v -> handleCreateStore());
    }

    private void loadCategories() {
        // Disable button until categories load
        btnCreate.setEnabled(false);

        // Use the Controller, NOT the Repo directly
        categoryController.getAll(new OperationCallback<List<StoreCategory>>() {
            @Override
            public void onResult(Result<List<StoreCategory>> result) {
                switch (result.status) {
                    case LOADING:
                        // Optional: Show small spinner
                        break;
                    case SUCCESS:
                        List<StoreCategory> categories = result.data;
                        if (categories == null || categories.isEmpty()) {
                            Toast.makeText(CreateStoreActivity.this, "No categories found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Populate Spinner
                        ArrayAdapter<StoreCategory> adapter = new ArrayAdapter<>(
                                CreateStoreActivity.this,
                                android.R.layout.simple_spinner_item,
                                categories
                        );
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCategory.setAdapter(adapter);

                        btnCreate.setEnabled(true);
                        break;
                    case ERROR:
                        Toast.makeText(CreateStoreActivity.this, "Error: " + result.message, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void handleCreateStore() {
        String name = etName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        StoreCategory selectedCategory = (StoreCategory) spinnerCategory.getSelectedItem();

        // Call StoreController
        storeController.add(name, address, phone, "Rp.", selectedCategory, currentUser, new OperationCallback<String>() {
            @Override
            public void onResult(Result<String> result) {
                switch (result.status) {
                    case LOADING:
                        progressBar.setVisibility(View.VISIBLE);
                        btnCreate.setVisibility(View.INVISIBLE);
                        break;

                    case SUCCESS:
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(CreateStoreActivity.this, "Store Created!", Toast.LENGTH_SHORT).show();

                        // Navigate to Dashboard
                        Intent intent = new Intent(CreateStoreActivity.this, MainActivity.class);
                        // We don't need to pass store ID, MainActivity will fetch it
                        startActivity(intent);
                        finish();
                        break;

                    case ERROR:
                        progressBar.setVisibility(View.GONE);
                        btnCreate.setVisibility(View.VISIBLE);
                        Toast.makeText(CreateStoreActivity.this, result.message, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }
}