package com.example.beesness.views.POS; // Kept your package name

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout; // Import added
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
import com.example.beesness.views.facade.SetupNavigationFacade;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// 1. ADDED: Implements interface
public class POSActivity extends AppCompatActivity implements CartSheetFragment.CartUpdateListener {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private TextView tvTotal;
    private Button btnCheckout;
    private LinearLayout checkoutLayout; // 2. ADDED: Reference to the clickable total area

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

        checkoutLayout = findViewById(R.id.checkoutLayout);
        checkoutLayout.setOnClickListener(v -> openCartEditor());

        recyclerView = findViewById(R.id.rvPosProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bottomNav = findViewById(R.id.bottom_navigation);
        setupNavigation();

        adapter = new ProductAdapter();

        adapter.setOnItemClickListener(product -> {
            addToCart(product);
        });

        recyclerView.setAdapter(adapter);
    }

    // 4. ADDED: Logic to open the fragment
    private void openCartEditor() {
        if (cartList.isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        // Pass the live cartList to the fragment
        CartSheetFragment fragment = new CartSheetFragment(cartList, this);
        fragment.show(getSupportFragmentManager(), "CartEdit");
    }

    // 5. ADDED: Callback from the fragment when user adds/removes items
    @Override
    public void onCartUpdated() {
        updateTotalUI();
        // Optional: If you want to refresh the main product list stock numbers
        // when items are removed from cart, you could call loadProducts(storeId) here.
    }

    @Override
    public void onStockRestored(String productId, int quantityRestored) {
        if (adapter != null && adapter.productList != null) {
            for (Product p : adapter.productList) {
                if (p.getId().equals(productId)) {

                    int newStock = p.getQuantity() + quantityRestored;
                    p.setQuantity(newStock);

                    adapter.notifyDataSetChanged();
                    return;
                }
            }
        }
    }

    private void addToCart(Product originalProduct) {
        // 1. Check stock
        if (originalProduct.getQuantity() <= 0) {
            Toast.makeText(this, "Out of Stock!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. VISUAL UPDATE: Decrement immediately so user sees it
        originalProduct.setQuantity(originalProduct.getQuantity() - 1);
        adapter.notifyDataSetChanged();

        // 3. Add to Cart Logic
        boolean found = false;
        for (Product cartItem : cartList) {
            if (cartItem.getId().equals(originalProduct.getId())) {
                // We just increment the cart count
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                found = true;
                break;
            }
        }

        if (!found) {
            Product cartItem = new Product();
            cartItem.setId(originalProduct.getId());
            cartItem.setName(originalProduct.getName());
            cartItem.setSellPrice(originalProduct.getSellPrice());
            cartItem.setBuyPrice(originalProduct.getBuyPrice());
            cartItem.setStoreId(originalProduct.getStoreId());
            cartItem.setQuantity(1);

            cartList.add(cartItem);
        }

        updateTotalUI();
    }

    private void updateTotalUI() {
        currentTotal = 0;
        for (Product p : cartList) {
            currentTotal += (p.getSellPrice() * p.getQuantity());
        }

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

                cartList.clear();
                currentTotal = 0;
                updateTotalUI();

            } else if (result.status == Result.Status.ERROR) {
                Toast.makeText(this, "Failed: " + result.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProducts(String storeId) {
        productController.getAll(storeId, result -> {
            if (result.status == Result.Status.SUCCESS) {
                adapter.setProducts(result.data);
            }
        });
    }

    private void setupNavigation() {
        SetupNavigationFacade navFacade = new SetupNavigationFacade(this, bottomNav);
        navFacade.setupNavigation(R.id.nav_cart);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}