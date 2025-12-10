package com.example.beesness.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.beesness.R;
import com.example.beesness.controller.StoreController;
import com.example.beesness.models.Store;
import com.example.beesness.models.User;
import com.example.beesness.utils.OperationCallback;
import com.example.beesness.utils.Result;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // UI Components
    private Spinner spinnerStore;
    private String storeId;
    private TextView tvTotalRevenue, tvTotalExpense;
    private BottomNavigationView bottomNav;
    // Removed FloatingActionButton

    // Controllers & Data
    private StoreController storeController;
    private User currentUser;
    private List<Store> userStores = new ArrayList<>();
    private ImageButton addStoreBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 1. Get User from Intent
        currentUser = (User) getIntent().getSerializableExtra("USER");

        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }


        storeController = new StoreController();
        loadUserStores();
        initViews();

        setupNavigation();

        if(getIntent().getStringExtra("storeId") != null) storeId = getIntent().getStringExtra("storeId");
        if(storeId == null) storeId = userStores.get(0).getId();
    }

    private void initViews() {
        spinnerStore = findViewById(R.id.spinnerStoreSelector);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        tvTotalExpense = findViewById(R.id.tvTotalExpense);
        bottomNav = findViewById(R.id.bottom_navigation);
        addStoreBtn = findViewById(R.id.addStoreButton);
        initBtn();
    }

    private void initBtn(){
        addStoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateStoreActivity.class);
                intent.putExtra("USER", currentUser);
                intent.putExtra("hasStore", true);
                if(!storeId.isEmpty()) intent.putExtra("storeId", storeId);
                startActivity(intent);
            }
        });
    }

    // --- LOGIC: Load Stores owned by this User ---
    private void loadUserStores() {
        storeController.getByOwnerId(currentUser, new OperationCallback<List<Store>>() {
            @Override
            public void onResult(Result<List<Store>> result) {
                switch (result.status) {
                    case LOADING:
                        break;
                    case SUCCESS:
                        userStores = result.data;
                        if (userStores == null || userStores.isEmpty()) {
                            Toast.makeText(MainActivity.this, "Welcome! Please create your first store.", Toast.LENGTH_LONG).show();
                            // Redirect to CreateStoreActivity...
                        } else {
                            setupStoreSpinner();
                        }
                        break;
                    case ERROR:
                        Toast.makeText(MainActivity.this, "Error: " + result.message, Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }

    private void setupStoreSpinner() {
        int index = -1;
        List<String> storeNames = new ArrayList<>();
        for (Store s : userStores) {
            if(!storeId.isEmpty() && storeId.equals(s.getId())){
                index = userStores.indexOf(s);
            }
            storeNames.add(s.getName());
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, storeNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStore.setAdapter(adapter);

        if(index != -1) spinnerStore.setSelection(index);

        spinnerStore.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadDashboardData(userStores.get(position));
                storeId = userStores.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                storeId = userStores.get(0).getId();
            }
        });
    }

    private void loadDashboardData(Store store) {
        tvTotalRevenue.setText(store.getCurrency() + " 0");
        tvTotalExpense.setText(store.getCurrency() + " 0");
    }

    private void setupNavigation() {
        // Highlight "Home" by default
        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                return true;

            } else if (id == R.id.nav_transaction) {
                // This replaces your old FAB logic
                Toast.makeText(this, "Opening POS (Point of Sale)...", Toast.LENGTH_SHORT).show();
                // Intent intent = new Intent(MainActivity.this, TransactionActivity.class);
                // startActivity(intent);
                return true;

            } else if (id == R.id.nav_cart) {
                Toast.makeText(this, "Opening Orders...", Toast.LENGTH_SHORT).show();
                return true;

            } else if (id == R.id.nav_stock) {
                Intent intent = new Intent(MainActivity.this, StockActivity.class);
                intent.putExtra("USER", currentUser);
                intent.putExtra("storeId", storeId);
                startActivity(intent);
                return true;

            } else if (id == R.id.nav_profile) {
                Toast.makeText(this, "Opening Profile...", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }
}