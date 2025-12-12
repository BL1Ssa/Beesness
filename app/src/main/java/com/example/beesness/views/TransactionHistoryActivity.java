package com.example.beesness.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beesness.R;
import com.example.beesness.controller.TransactionController;
import com.example.beesness.database.repositories.TransactionRepository;
import com.example.beesness.models.Transaction;
import com.example.beesness.utils.FirestoreCallback;
import com.example.beesness.utils.SessionManager;
import com.example.beesness.views.adapters.TransactionAdapter;
import com.example.beesness.views.facade.SetupNavigationFacade;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class TransactionHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private TransactionController controller;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private BottomNavigationView bottomNav;

    private SessionManager sessionManager;
    private String currentStoreId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        // 1. Init Session & Check Login
        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            finish();
            return;
        }

        // 2. Get Active Store ID
        currentStoreId = sessionManager.getCurrentStoreId();
        if (currentStoreId == null || currentStoreId.isEmpty()) {
            Toast.makeText(this, "No Active Store Found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        controller = new TransactionController();
        setupNavigation();
        loadHistoryData();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.rvHistory);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        bottomNav = findViewById(R.id.bottom_navigation);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionAdapter();

        adapter.setOnItemClickListener(transaction -> {
            Intent intent = new Intent(TransactionHistoryActivity.this, TransactionDetailActivity.class);
            intent.putExtra("TRANSACTION_DATA", transaction);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
    }

    private void loadHistoryData() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmptyState.setVisibility(View.GONE);

        controller.getHistory(currentStoreId, result -> {
            switch (result.status) {
                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    tvEmptyState.setVisibility(View.GONE);
                    break;

                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    List<Transaction> list = result.data;

                    if (list != null && !list.isEmpty()) {
                        adapter.setTransactions(list);
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        tvEmptyState.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                    break;

                case ERROR:
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(TransactionHistoryActivity.this, result.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });

    }

    private void setupNavigation() {
        SetupNavigationFacade navFacade = new SetupNavigationFacade(this, bottomNav);
        navFacade.setupNavigation(R.id.nav_home);
    }
}