package com.example.beesness.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beesness.R;
import com.example.beesness.controller.ProductController;
import com.example.beesness.controller.TransactionController;
import com.example.beesness.models.Product;
import com.example.beesness.models.User;
import com.example.beesness.utils.Result;
import com.example.beesness.utils.SessionManager;
import com.example.beesness.views.adapters.ProductAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class POSActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private TextView tvTotal;
    private Button btnCheckout;

    private ProductController productController;
    private TransactionController transactionController;
    private User currentUser;

    private List<Product> cartList = new ArrayList<>();
    private double currentTotal = 0;
    private BottomNavigationView bottomNav;
    private String storeId;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pos);

        sessionManager = new SessionManager(this);
        // Setup Controllers
        productController = new ProductController();
        transactionController = new TransactionController();

        currentUser = sessionManager.getUserDetail();
        storeId = sessionManager.getCurrentStoreId();

        initViews();
        loadProducts(storeId);

        btnCheckout.setOnClickListener(v -> handleCheckout());
    }

    private void initViews() {
        tvTotal = findViewById(R.id.tvTotalAmount);
        btnCheckout = findViewById(R.id.btnCheckout);
        recyclerView = findViewById(R.id.rvPosProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bottomNav = findViewById(R.id.bottom_navigation);
        setupNavigation();

        adapter = new ProductAdapter();

        // CLICK LISTENER: ADD TO CART
        adapter.setOnItemClickListener(product -> {
            addToCart(product);
        });

        recyclerView.setAdapter(adapter);
    }

    private void addToCart(Product originalProduct) {
        // Logic: Create a copy or wrapper so we don't mess up the original list
        // For simplicity: We will just track "Cart Items" in a list

        // Check if enough stock
        if (originalProduct.getQuantity() <= 0) {
            Toast.makeText(this, "Out of Stock!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add to calculation
        currentTotal += originalProduct.getSellPrice();
        updateTotalUI();

        // Add to our internal cart list
        // Note: In a real app, you'd check if item exists and increment qty.
        // Here, we just add a "Product" object representing 1 unit sold.
        Product cartItem = new Product();
        cartItem.setId(originalProduct.getId()); // IMPORTANT: Keep ID
        cartItem.setName(originalProduct.getName());
        cartItem.setQuantity(1); // Sold 1

        cartList.add(cartItem);
        Toast.makeText(this, "Added " + originalProduct.getName(), Toast.LENGTH_SHORT).show();
    }

    private void updateTotalUI() {
        Locale localeID = new Locale("id", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        tvTotal.setText(formatRupiah.format(currentTotal));
    }

    private void handleCheckout() {
        if (cartList.isEmpty()) {
            Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        transactionController.processCheckout(cartList, storeId, currentTotal, result -> {
            if (result.status == Result.Status.SUCCESS) {
                Toast.makeText(this, "Transaction Success!", Toast.LENGTH_LONG).show();
                // Clear Cart
                cartList.clear();
                currentTotal = 0;
                updateTotalUI();
                // Ideally refresh the product list here to show new stock
            } else if (result.status == Result.Status.ERROR) {
                Toast.makeText(this, "Failed: " + result.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProducts(String storeId) {
        // ... (Copy load logic from StockActivity) ...
        productController.getAll(storeId, result -> {
            if (result.status == Result.Status.SUCCESS) {
                adapter.setProducts(result.data);
            }
        });
    }

    private void setupNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_cart);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                Intent intent = new Intent(POSActivity.this, MainActivity.class);
                startActivity(intent);
                return true;

            } else if (id == R.id.nav_transaction) {
                 Intent intent = new Intent(POSActivity.this, TransactionHistoryActivity.class);
                 startActivity(intent);
                return true;

            } else if (id == R.id.nav_cart) {
                return true;

            } else if (id == R.id.nav_stock) {
                Intent intent = new Intent(POSActivity.this, StockActivity.class);
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