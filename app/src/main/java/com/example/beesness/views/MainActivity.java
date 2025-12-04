package com.example.beesness.views;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.beesness.R;
import com.example.beesness.utils.FirestoreCallback;
import com.example.beesness.database.repositories.StaffRepository;
import com.example.beesness.models.Staff;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // 1. UI Components
    private Spinner spinnerStore;
    private TextView tvTotalRevenue, tvTotalExpense;
    private BottomNavigationView bottomNav;

    // 2. Data & Logic
    private StaffRepository staffRepo;

    // TEMPORARY: Hardcoded User ID for development.
    // Later, this will come from LoginActivity.
    private final String CURRENT_USER_ID = "USER_123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Ensure this matches your XML filename

        // A. Initialize Views
        spinnerStore = findViewById(R.id.spinnerStoreSelector);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        tvTotalExpense = findViewById(R.id.tvTotalExpense);
        bottomNav = findViewById(R.id.bottom_navigation);

        // B. Initialize Repository
        staffRepo = StaffRepository.getInstance();

        // C. Start Logic
        loadUserStores();
        setupNavigation();
    }

    // --- LOGIC: Load the Stores this user owns ---
    private void loadUserStores() {
        staffRepo.getByUserId(CURRENT_USER_ID, new FirestoreCallback<List<Staff>>() {
            @Override
            public void onSuccess(List<Staff> staffRecords) {
                if (staffRecords.isEmpty()) {
                    // Scenario: User has no store. Force them to create one.
                    Toast.makeText(MainActivity.this, "Please create your first store!", Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(MainActivity.this, CreateStoreActivity.class);
//                    startActivity(intent);
//                    finish(); // Close MainActivity so they can't go back
                    return;
                }

                // Scenario: User has stores. Populate the Spinner.
                setupStoreSpinner(staffRecords);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MainActivity.this, "Error loading stores: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- LOGIC: Populate Spinner & Handle Switching ---
    private void setupStoreSpinner(List<Staff> staffList) {
        // 1. Extract Store IDs (Ideally, fetch Names, but IDs are fine for MVP)
        List<String> storeIds = new ArrayList<>();
        for (Staff s : staffList) {
            storeIds.add(s.getStoreId());
        }

        // 2. Create Adapter for the Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, storeIds);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStore.setAdapter(adapter);

        // 3. Listen for Selection Changes
        spinnerStore.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedStoreId = storeIds.get(position);

                // MOCK DATA: Update the Dashboard numbers based on selection
                updateDashboardNumbers(selectedStoreId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // --- LOGIC: Update UI (Mock for now) ---
    private void updateDashboardNumbers(String storeId) {
        // Later, you will call OrderRepository here to get real math.
        // For now, let's just show it works.
        tvTotalRevenue.setText("Rp 15.000.000");
        tvTotalExpense.setText("Rp 5.000.000");
        Toast.makeText(this, "Switched to store: " + storeId, Toast.LENGTH_SHORT).show();
    }

    // --- LOGIC: Bottom Navigation ---
    private void setupNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_stock) {
                // TODO: Link to ProductListActivity
                Toast.makeText(this, "Stock Management clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_profile) {
                Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show();
                return true;
            }else if(id == R.id.nav_cart) {
                Toast.makeText(this, "Cart clicked", Toast.LENGTH_SHORT).show();
                return true;
            }else if(id == R.id.nav_transaction){
                Toast.makeText(this, "Transaction clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }
}