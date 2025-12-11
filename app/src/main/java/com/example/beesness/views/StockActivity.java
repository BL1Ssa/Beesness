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
import com.example.beesness.controller.ProductController;
import com.example.beesness.controller.StoreController;
import com.example.beesness.models.Product;
import com.example.beesness.models.Store;
import com.example.beesness.models.User;
import com.example.beesness.utils.Result;
import com.example.beesness.utils.SessionManager;
import com.example.beesness.views.adapters.ProductAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.beesness.views.facade.SetupNavigationFacade;

import java.util.List;

public class StockActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyStateText;
    private FloatingActionButton fabAdd;
    private ExtendedFloatingActionButton btnProcurement;

    private ProductController productController;
    private StoreController storeController;
    private User currentUser;
    private String currentStoreId;
    private BottomNavigationView bottomNav;
    private SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        bottomNav = findViewById(R.id.bottom_navigation);

        sessionManager = new SessionManager(this);
        currentUser = sessionManager.getUserDetail();
        currentStoreId = sessionManager.getCurrentStoreId();
        if (currentStoreId == null) {
            Toast.makeText(this, "No store selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        productController = new ProductController();
        storeController = new StoreController();

        initViews();
        setupRecyclerView();
        setupNavigation();

        if (currentUser != null) {
            fetchStoreAndProducts();
        } else {
            Toast.makeText(this, "Session Error: No user found.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list automatically when returning from "Add Product"
        if (currentStoreId != null) {
            loadProducts(currentStoreId);
        }
    }

    private void initViews() {
        progressBar = findViewById(R.id.progressBar);
        emptyStateText = findViewById(R.id.tvEmptyState);
        fabAdd = findViewById(R.id.fabAddProduct);
        btnProcurement = findViewById(R.id.btnProcurement);

        fabAdd.setOnClickListener(v -> {
            if (currentStoreId != null) {
                Intent intent = new Intent(StockActivity.this, AddProductActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please wait, loading store info...", Toast.LENGTH_SHORT).show();
            }
        });

        btnProcurement.setOnClickListener(v -> {
            if (currentStoreId != null) {
                Intent intent = new Intent(StockActivity.this, ProcurementActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Store not loaded yet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.rvProducts);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ProductAdapter();

        adapter.setOnItemClickListener(product -> {
            Intent intent = new Intent(StockActivity.this, AddProductActivity.class);
            intent.putExtra("STORE_ID", currentStoreId);
            intent.putExtra("PRODUCT_TO_EDIT", product);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
    }

    private void fetchStoreAndProducts() {
        showLoading(true);
        storeController.getByOwnerId(currentUser, result -> {
            if (result.status == Result.Status.SUCCESS) {
                List<Store> stores = result.data;

                if (stores != null && !stores.isEmpty()) {
                    if(currentStoreId.isEmpty()) currentStoreId = stores.get(0).getId();
                    loadProducts(currentStoreId);
                } else {
                    showLoading(false);
                    Toast.makeText(StockActivity.this, "No store found.", Toast.LENGTH_LONG).show();
                }
            } else if (result.status == Result.Status.ERROR) {
                showLoading(false);
                Toast.makeText(StockActivity.this, "Error: " + result.message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadProducts(String storeId) {
        productController.getAll(storeId, result -> {
            showLoading(false);

            if (result.status == Result.Status.SUCCESS) {
                List<Product> products = result.data;

                if (products != null && !products.isEmpty()) {
                    adapter.setProducts(products);
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyStateText.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    emptyStateText.setVisibility(View.VISIBLE);
                }
            } else if (result.status == Result.Status.ERROR) {
                Toast.makeText(StockActivity.this, result.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            emptyStateText.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setupNavigation(){
        SetupNavigationFacade navFacade = new SetupNavigationFacade(this,bottomNav);
        navFacade.setupNavigation(R.id.nav_stock);
    }
}


