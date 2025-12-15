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
import com.example.beesness.utils.SessionManager;
import com.example.beesness.views.facade.SetupNavigationFacade;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import java.util.Calendar;
import java.util.Collections;

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

    private TextView tvTotalProfit;

    private BarChart barChart;

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
        tvTotalProfit = findViewById(R.id.tvTotalProfit);
        barChart = findViewById(R.id.barChart);

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
            intent.putExtra("hasStore", true);
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

                        double profit = salesTotal - procurementTotal;

                        Locale localeID = new Locale("id", "ID");
                        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
                        tvTotalProfit.setText(formatRupiah.format(profit));
                        // Dynamic Color
                        if (profit >= 0) {
                            tvTotalProfit.setTextColor(getResources().getColor(R.color.green_growth));
                        } else {
                            tvTotalProfit.setTextColor(getResources().getColor(R.color.red_growth_line));
                        }

                        setRevenueExpenseText();
                        setupWeeklyChart(result.data);
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

    private void setupWeeklyChart(List<Transaction> transactions) {
        List<String> last7Days = new ArrayList<>();
        List<Double> dailyRevenue = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", Locale.US);
        Calendar calendar = Calendar.getInstance();


        for (int i = 0; i < 7; i++) {
            dailyRevenue.add(0.0);
            last7Days.add("");
        }


        for (int i = 6; i >= 0; i--) {
            last7Days.set(i, sdf.format(calendar.getTime()));

            double dayTotal = 0;
            for (Transaction t : transactions) {
                // Only count SALES, ignore "Procurement"
                if ("SALE".equals(t.getType())) {
                    if (isSameDay(t.getDate(), calendar.getTime())) {
                        dayTotal += t.getTotalAmount();
                    }
                }
            }
            dailyRevenue.set(i, dayTotal);
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }


        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            entries.add(new BarEntry(i, dailyRevenue.get(i).floatValue()));
        }


        BarDataSet dataSet = new BarDataSet(entries, "Revenue");
        dataSet.setColor(getResources().getColor(R.color.honey_primary));
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        barChart.getXAxis().setTextColor(getResources().getColor(R.color.gray_thin_text));
        barChart.getLegend().setTextColor(getResources().getColor(R.color.gray_thin_text));
        barChart.getBarData().setValueTextColor(getResources().getColor(R.color.gray_thin_text));
        barChart.getAxisLeft().setTextColor(getResources().getColor(R.color.gray_thin_text));
        barChart.getDescription().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(last7Days));
        barChart.getXAxis().setGranularity(1f);
        barChart.setFitBars(true);
        barChart.animateY(1000); // Animation!
        barChart.invalidate();   // Refresh
    }

    // Small helper to compare dates
    private boolean isSameDay(java.util.Date date1, java.util.Date date2) {
        if(date1 == null || date2 == null) return false;
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd", Locale.US);
        return fmt.format(date1).equals(fmt.format(date2));
    }

}