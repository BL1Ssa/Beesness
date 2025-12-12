package com.example.beesness.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.example.beesness.utils.SessionManager; // <--- IMPORT THIS
import com.example.beesness.views.facade.SetupNavigationFacade;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerStore;
    private TextView tvTotalRevenue, tvTotalExpense;
    private BottomNavigationView bottomNav;
    private ImageButton addStoreBtn;

    private StoreController storeController;
    private SessionManager sessionManager;
    private User currentUser;
    private List<Store> userStores = new ArrayList<>();
    private String currentStoreId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        currentUser = sessionManager.getUserDetail();

        storeController = new StoreController();

        initViews();
        loadUserStores();
        setupNavigation();
    }

    private void initViews() {
        spinnerStore = findViewById(R.id.spinnerStoreSelector);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        tvTotalExpense = findViewById(R.id.tvTotalExpense);
        bottomNav = findViewById(R.id.bottom_navigation);
        addStoreBtn = findViewById(R.id.addStoreButton);

        initBtn();
    }

    private void initBtn() {
        addStoreBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateStoreActivity.class);
            intent.putExtra("hasStore", true); // This logic is fine to keep if specific
            startActivity(intent);
        });
    }
    private void loadUserStores() {
        storeController.getByOwnerId(currentUser, new OperationCallback<List<Store>>() {
            @Override
            public void onResult(Result<List<Store>> result) {
                switch (result.status) {
                    case LOADING:
                        // Optional: Show a progress bar
                        break;
                    case SUCCESS:
                        userStores = result.data;
                        if (userStores == null || userStores.isEmpty()) {
                            Toast.makeText(MainActivity.this, "Welcome! Please create your first store.", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(MainActivity.this, CreateStoreActivity.class));
                            finish();
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
        List<String> storeNames = new ArrayList<>();
        for (Store s : userStores) {
            storeNames.add(s.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, storeNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStore.setAdapter(adapter);

        String savedStoreId = sessionManager.getCurrentStoreId();
        int selectedIndex = 0;

        if (savedStoreId != null) {
            for (int i = 0; i < userStores.size(); i++) {
                if (userStores.get(i).getId().equals(savedStoreId)) {
                    selectedIndex = i;
                    break;
                }
            }
        }

        spinnerStore.setSelection(selectedIndex);

        currentStoreId = userStores.get(selectedIndex).getId();
        sessionManager.saveCurrentStore(currentStoreId);
        loadDashboardData(userStores.get(selectedIndex));

        spinnerStore.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Store selectedStore = userStores.get(position);

                sessionManager.saveCurrentStore(selectedStore.getId());
                currentStoreId = selectedStore.getId();

                loadDashboardData(selectedStore);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadDashboardData(Store store) {
        tvTotalRevenue.setText(store.getCurrency() + " 0");
        tvTotalExpense.setText(store.getCurrency() + " 0");
    }

    private void setupNavigation(){
        //SetupNavigation logic is on Facade
        SetupNavigationFacade navFacade = new SetupNavigationFacade(this,bottomNav);
        navFacade.setupNavigation(R.id.nav_home);
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}