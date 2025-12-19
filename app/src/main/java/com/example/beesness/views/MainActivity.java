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

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerStore;
    private TextView tvTotalRevenue, tvTotalExpense;
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

    private TextView tvTransactionCount, tvAvgSale;

    private TextView tvTodayRevenue, tvTodayExpense, tvTodayProfit;

    private TextView tvTodayDate;

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

    @Override
    protected void onResume() {
        setupNavigation();
        super.onResume();
    }

    private void initViews() {
        spinnerStore = findViewById(R.id.spinnerStoreSelector);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        tvTotalExpense = findViewById(R.id.tvTotalExpense);
        bottomNav = findViewById(R.id.bottom_navigation);
        addStoreBtn = findViewById(R.id.addStoreButton);
        tvTotalProfit = findViewById(R.id.tvTotalProfit);
        barChart = findViewById(R.id.barChart);
        tvTransactionCount = findViewById(R.id.tvTransactionCount);
        tvAvgSale = findViewById(R.id.tvAvgSale);
        tvTodayRevenue = findViewById(R.id.tvTodayRevenue);
        tvTodayExpense = findViewById(R.id.tvTodayExpense);
        tvTodayProfit = findViewById(R.id.tvTodayProfit);
        tvTodayDate = findViewById(R.id.tvTodayDate);

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
        // todays date
        Date today = new Date();
        Locale localeID = new Locale("id", "ID");
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", localeID);
        tvTodayDate.setText(dateFormat.format(today));

        transactionController.getHistory(store.getId(), new OperationCallback<List<Transaction>>() {
            @Override
            public void onResult(Result<List<Transaction>> result) {
                switch (result.status) {
                    case LOADING: break;
                    case SUCCESS:
                        initValues();

                        double todayRev = 0;
                        double todayExp = 0;
                        int todaySaleCount = 0;

                        Date today = new Date();

                        for(Transaction t : result.data){
                            if(t.getType().equals("SALE")){
                                salesTotal += t.getTotalAmount();
                            } else {
                                procurementTotal += t.getTotalAmount();
                            }

                            if (isSameDay(t.getDate(), today)) {
                                if(t.getType().equals("SALE")){
                                    todayRev += t.getTotalAmount();
                                    todaySaleCount++;
                                } else {
                                    todayExp += t.getTotalAmount();
                                }
                            }
                        }

                        Locale localeID = new Locale("id", "ID");
                        NumberFormat fmt = NumberFormat.getCurrencyInstance(localeID);

                        tvTotalRevenue.setText(fmt.format(salesTotal));
                        tvTotalExpense.setText(fmt.format(procurementTotal));

                        double allTimeProfit = salesTotal - procurementTotal;
                        tvTotalProfit.setText(fmt.format(allTimeProfit));
                        tvTotalProfit.setTextColor(allTimeProfit >= 0 ?
                                getResources().getColor(R.color.green_growth) :
                                getResources().getColor(R.color.red_growth_line));


                        double avgSaleValue = (todaySaleCount > 0) ? (todayRev / todaySaleCount) : 0;
                        tvTransactionCount.setText(String.valueOf(todaySaleCount));
                        tvAvgSale.setText(fmt.format(avgSaleValue));

                        tvTodayRevenue.setText(fmt.format(todayRev));
                        tvTodayExpense.setText(fmt.format(todayExp));

                        double todayProfit = todayRev - todayExp;
                        tvTodayProfit.setText(fmt.format(todayProfit));
                        tvTodayProfit.setTextColor(todayProfit >= 0 ?
                                getResources().getColor(R.color.green_growth) :
                                getResources().getColor(R.color.red_growth_line));

                        setupComparisonChart(result.data);
                        break;
                    case ERROR:
                        Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
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

    private void setupComparisonChart(List<Transaction> transactions) {
        List<String> last7Days = new ArrayList<>();
        List<Double> dailyRevenue = new ArrayList<>();
        List<Double> dailyExpense = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", Locale.US);
        Calendar calendar = Calendar.getInstance();

        // make list to 0
        for (int i = 0; i < 7; i++) {
            dailyRevenue.add(0.0);
            dailyExpense.add(0.0);
            last7Days.add("");
        }

        // fill data (Today -> 6 days ago)
        for (int i = 6; i >= 0; i--) {
            last7Days.set(i, sdf.format(calendar.getTime()));

            double revTotal = 0;
            double expTotal = 0;

            for (Transaction t : transactions) {
                if (isSameDay(t.getDate(), calendar.getTime())) {
                    if ("SALE".equals(t.getType())) {
                        revTotal += t.getTotalAmount();
                    } else {
                        expTotal += t.getTotalAmount();
                    }
                }
            }
            dailyRevenue.set(i, revTotal);
            dailyExpense.set(i, expTotal);
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }

        //add to bar entry object
        List<BarEntry> revenueEntries = new ArrayList<>();
        List<BarEntry> expenseEntries = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            revenueEntries.add(new BarEntry(i, dailyRevenue.get(i).floatValue()));
            expenseEntries.add(new BarEntry(i, dailyExpense.get(i).floatValue()));
        }

        // datasets
        BarDataSet set1 = new BarDataSet(revenueEntries, "Income");
        set1.setColor(getResources().getColor(R.color.green_growth)); // Green
        set1.setValueTextColor(getResources().getColor(R.color.gray_thin_text));
        set1.setValueTextSize(10f);

        BarDataSet set2 = new BarDataSet(expenseEntries, "Expense");
        set2.setColor(getResources().getColor(R.color.red_growth_line)); // Red
        set2.setValueTextColor(getResources().getColor(R.color.gray_thin_text));
        set2.setValueTextSize(10f);

        // combine data
        BarData data = new BarData(set1, set2);
        barChart.setData(data);

        // grouping
        float groupSpace = 0.4f; // Space between  days
        float barSpace = 0.05f; // Space between green and red bar
        float barWidth = 0.25f; // bar thickness

        data.setBarWidth(barWidth);

        // styliing
        int grayColor = getResources().getColor(R.color.gray_thin_text);

        barChart.getXAxis().setTextColor(grayColor);
        barChart.getAxisLeft().setTextColor(grayColor);
        barChart.getLegend().setTextColor(grayColor);

        barChart.getXAxis().setCenterAxisLabels(true);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(last7Days));
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setAxisMinimum(0f);
        barChart.getXAxis().setAxisMaximum(7f);

        barChart.getDescription().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);

        // apply
        barChart.groupBars(0f, groupSpace, barSpace);
        barChart.animateY(1000);
        barChart.invalidate();
    }


    private boolean isSameDay(java.util.Date date1, java.util.Date date2) {
        if(date1 == null || date2 == null) return false;
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd", Locale.US);
        return fmt.format(date1).equals(fmt.format(date2));
    }

}