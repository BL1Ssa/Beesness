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
import com.example.beesness.views.adapters.ProductAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class StockActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyStateText;
    private FloatingActionButton fabAdd;

    private ProductController productController;
    private StoreController storeController;
    private User currentUser;
    private String currentStoreId;
    private BottomNavigationView bottomNav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        bottomNav = findViewById(R.id.bottom_navigation);
        productController = new ProductController();
        storeController = new StoreController();

        currentUser = (User) getIntent().getSerializableExtra("USER");

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

        fabAdd.setOnClickListener(v -> {
            if (currentStoreId != null) {
                Intent intent = new Intent(StockActivity.this, AddProductActivity.class);
                intent.putExtra("STORE_ID", currentStoreId);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please wait, loading store info...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.rvProducts);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ProductAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void fetchStoreAndProducts() {
        showLoading(true);
        storeController.getByOwnerId(currentUser, result -> {
            if (result.status == Result.Status.SUCCESS) {
                List<Store> stores = result.data;

                if (stores != null && !stores.isEmpty()) {
                    currentStoreId = stores.get(0).getId();
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

    private void setupNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_stock);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                Intent intent = new Intent(StockActivity.this, MainActivity.class);
                intent.putExtra("USER", currentUser);
                startActivity(intent);
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
//                Intent intent = new Intent(StockActivity.this, StockActivity.class);
//                intent.putExtra("USER", currentUser);
//                startActivity(intent);
                return true;

            } else if (id == R.id.nav_profile) {
                Toast.makeText(this, "Opening Profile...", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }
}