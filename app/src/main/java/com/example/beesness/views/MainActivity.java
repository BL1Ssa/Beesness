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
import com.example.beesness.controller.TransactionController;
import com.example.beesness.models.Store;
import com.example.beesness.models.Transaction;
import com.example.beesness.models.User;
import com.example.beesness.utils.OperationCallback;
import com.example.beesness.utils.Result;
import com.example.beesness.utils.SessionManager; // <--- IMPORT THIS
import com.example.beesness.views.facade.SetupNavigationFacade;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerStore;
    private TextView tvTotalRevenue, tvTotalExpense, tvDateRevenue, tvDateExpense;
    private BottomNavigationView bottomNav;
    private ImageButton addStoreBtn;

    TransactionController transactionController;
    private StoreController storeController;
    private SessionManager sessionManager;
    private User currentUser;
    private List<Store> userStores = new ArrayList<>();
    private String currentStoreId;
    private double salesTotal, procurementTotal;

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
        transactionController = new TransactionController();

        initViews();
        loadUserStores();
        setupNavigation();
    }

    private void initViews() {
        spinnerStore = findViewById(R.id.spinnerStoreSelector);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        tvTotalExpense = findViewById(R.id.tvTotalExpense);
        tvDateRevenue = findViewById(R.id.tvDateRevenue);
        tvDateExpense = findViewById(R.id.tvDateExpense);
        bottomNav = findViewById(R.id.bottom_navigation);
        addStoreBtn = findViewById(R.id.addStoreButton);

        initBtn();
        initValues();
    }

    private void initValues() {
        salesTotal = 0;
        procurementTotal = 0;
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
        setDate();

        transactionController.getHistory(store.getId(), new OperationCallback<List<Transaction>>() {
            @Override
            public void onResult(Result<List<Transaction>> result) {
                switch (result.status) {
                    case LOADING:
                        break;
                    case SUCCESS:
                        initValues();
                        for(Transaction t : result.data){
                            if(t.getType().equals("SALE")){
                                salesTotal += t.getTotalAmount();
                            }
                            else {
                                procurementTotal += t.getTotalAmount();
                            }
                        }
                        setRevenueExpenseText();
                        break;
                    case ERROR:
                        Toast.makeText(MainActivity.this, "Not able to fetch transactions", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void setRevenueExpenseText() {
        Locale localeID = new Locale("id", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        tvTotalRevenue.setText(formatRupiah.format(salesTotal));
        tvTotalRevenue.setTextColor(getResources().getColor(R.color.green_growth));
        tvTotalExpense.setText(formatRupiah.format(procurementTotal));
        tvTotalExpense.setTextColor(getResources().getColor(R.color.red_growth_line));
    }

    private void setDate() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
        String formattedDate = simpleDateFormat.format(date);
        tvDateRevenue.setText("per " + formattedDate);
        tvDateExpense.setText("per " + formattedDate);
    }

    private void setupNavigation() {
        SetupNavigationFacade navFacade = new SetupNavigationFacade(this, bottomNav);
        navFacade.setupNavigation(R.id.nav_home);
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}