package com.example.beesness.views;

import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.beesness.R;
import com.example.beesness.controller.StoreCategoryController;
import com.example.beesness.controller.StoreController;
import com.example.beesness.models.StoreCategory;
import com.example.beesness.models.User;
import com.example.beesness.utils.OperationCallback;
import com.example.beesness.utils.Result;
import com.example.beesness.utils.SessionManager;

import java.util.List;

public class CreateStoreActivity extends AppCompatActivity {

    private EditText etName, etAddress, etPhone;
    private TextView tvTitle, tvDesc;
    private Spinner spinnerCategory;
    private Button btnCreate, btnCancel;
    private ProgressBar progressBar;

    private StoreController storeController;
    private StoreCategoryController categoryController;
    private User currentUser;
    private boolean hasStore;
    private String storeId;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_store);

        sessionManager = new SessionManager(this);

        if (checkUser()) return;
        initializeUserInputComponents();
        checkHasStore();
        initializeControllers();
        loadCategories();

        btnCreate.setOnClickListener(v -> {
            currentUser = sessionManager.getUserDetail();
            handleCreateStore();
        });
    }

    private void checkHasStore() {
        String currentId = sessionManager.getCurrentStoreId();
        hasStore = currentId != null && !currentId.isEmpty();

        if (hasStore) {
            tvTitle.setText("Add Another Business");
            tvDesc.setText("Enter your store details below to add another store");
            storeId = currentId;
            initCancelBtn();
        }
    }

    private boolean checkUser() {
        currentUser = sessionManager.getUserDetail();
        if (currentUser == null) {
            Toast.makeText(this, "Session Error: Please Login Again", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return false;
    }

    private void initializeControllers() {
        storeController = new StoreController();
        categoryController = new StoreCategoryController();
    }

    private void initializeUserInputComponents() {
        tvTitle = findViewById(R.id.tvTitleCreateStore);
        tvDesc = findViewById(R.id.tvDescCreateStore);
        etName = findViewById(R.id.etStoreName);
        etAddress = findViewById(R.id.etStoreAddress);
        etPhone = findViewById(R.id.etStorePhone);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnCreate = findViewById(R.id.btnCreateStore);
        progressBar = findViewById(R.id.progressBar);
        btnCancel = findViewById(R.id.cancelButton);
    }

    private void initCancelBtn() {
        btnCancel.setEnabled(true);
        btnCancel.setVisibility(VISIBLE);
        btnCancel.setOnClickListener(v -> {
            finish();
        });
    }

    private void loadCategories() {
        btnCreate.setEnabled(false);

        categoryController.getAll(new OperationCallback<List<StoreCategory>>() {
            @Override
            public void onResult(Result<List<StoreCategory>> result) {
                switch (result.status) {
                    case LOADING:
                        // Optional: Show loading state
                        break;
                    case SUCCESS:
                        List<StoreCategory> categories = result.data;
                        if (categories == null || categories.isEmpty()) {
                            Toast.makeText(CreateStoreActivity.this, "No categories found", Toast.LENGTH_SHORT).show();
                            return;
                        }
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

        // Basic Validation
        if(name.isEmpty() || address.isEmpty() || phone.isEmpty()){
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        storeController.add(name, address, phone, "Rp.", selectedCategory, currentUser, new OperationCallback<String>() {
            @Override
            public void onResult(Result<String> result) {
                switch (result.status) {
                    case LOADING:
                        progressBar.setVisibility(VISIBLE);
                        btnCreate.setVisibility(View.INVISIBLE);
                        break;

                    case SUCCESS:
                        progressBar.setVisibility(View.GONE);
                        String newStoreId = result.data;

                        sessionManager.saveCurrentStore(newStoreId);

                        Toast.makeText(CreateStoreActivity.this, "Store Created!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(CreateStoreActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        break;

                    case ERROR:
                        progressBar.setVisibility(View.GONE);
                        btnCreate.setVisibility(VISIBLE);
                        Toast.makeText(CreateStoreActivity.this, result.message, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }
}