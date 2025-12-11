package com.example.beesness.views;

import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beesness.R;
import com.example.beesness.controller.ProductController;
import com.example.beesness.controller.TransactionController;
import com.example.beesness.models.Product;
import com.example.beesness.utils.Result;
import com.example.beesness.utils.SessionManager;
import com.example.beesness.views.adapters.ProductAdapter;
// Ensure you import the Fragment and Interface correctly based on where you put them
import com.example.beesness.views.POS.CartSheetFragment;
import com.example.beesness.views.facade.SetupNavigationFacade;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProcurementActivity extends AppCompatActivity implements CartSheetFragment.CartUpdateListener {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private TextView tvTotal;
    private Button btnConfirm;
    private LinearLayout checkoutLayout;
    private BottomNavigationView bottomNav;

    private ProductController productController;
    private TransactionController transactionController;
    private SessionManager sessionManager;
    private String storeId;

    // List to hold items we are buying (Incoming Stock)
    private List<Product> procurementList = new ArrayList<>();
    private double estimatedCost = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pos);

        TextView headerTitle = findViewById(R.id.headerTitle);
        if(headerTitle != null) headerTitle.setText("Restock Inventory");

        sessionManager = new SessionManager(this);
        productController = new ProductController();
        transactionController = new TransactionController();
        storeId = sessionManager.getCurrentStoreId();

        initViews();
        loadProducts();
    }

    private void initViews() {
        tvTotal = findViewById(R.id.tvTotalAmount);

        btnConfirm = findViewById(R.id.btnCheckout);
        btnConfirm.setText("Confirm Restock"); // Rename button

        checkoutLayout = findViewById(R.id.checkoutLayout);
        checkoutLayout.setOnClickListener(v -> openCartEditor());

        recyclerView = findViewById(R.id.rvPosProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bottomNav = findViewById(R.id.bottom_navigation);
        setupNavigation();

        adapter = new ProductAdapter();
        adapter.setProcurementMode(true);
        // Custom Click Listener: Show Dialog instead of auto-add
        adapter.setOnItemClickListener(this::showRestockDialog);

        recyclerView.setAdapter(adapter);

        btnConfirm.setOnClickListener(v -> handleProcurement());
    }

    private void showRestockDialog(Product product) {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Quantity Received");
        int p = 40;
        input.setPadding(p, p, p, p);

        new AlertDialog.Builder(this)
                .setTitle("Restock: " + product.getName())
                .setMessage("Enter incoming quantity:")
                .setView(input)
                .setPositiveButton("Add", (dialog, which) -> {
                    String val = input.getText().toString();
                    if (!val.isEmpty()) {
                        int qty = Integer.parseInt(val);
                        addToProcurementList(product, qty);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addToProcurementList(Product original, int quantity) {
        // Check if already in list
        for (Product p : procurementList) {
            if (p.getId().equals(original.getId())) {
                p.setQuantity(p.getQuantity() + quantity);
                Toast.makeText(this, "Updated quantity!", Toast.LENGTH_SHORT).show();
                updateUI();
                return;
            }
        }

        // Add new item
        Product item = new Product();
        item.setId(original.getId());
        item.setName(original.getName());
        item.setBuyPrice(original.getBuyPrice());
        item.setSellPrice(original.getBuyPrice()); // lmao this is hardcoding
        item.setQuantity(quantity);

        procurementList.add(item);
        Toast.makeText(this, "Added to list", Toast.LENGTH_SHORT).show();
        updateUI();
    }

    private void openCartEditor() {
        if (procurementList.isEmpty()) {
            Toast.makeText(this, "List is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        CartSheetFragment fragment = new CartSheetFragment(procurementList, this);
        fragment.show(getSupportFragmentManager(), "CartEdit");
    }

    @Override
    public void onCartUpdated() {
        updateUI();
    }

    @Override
    public void onStockRestored(String productId, int quantityRestored) {
        // Not strictly needed for Procurement since we aren't visually decrementing stock from the list
        // but required by the interface.
    }

    private void updateUI() {
        estimatedCost = 0;
        int totalItems = 0;
        for (Product p : procurementList) {
            estimatedCost += (p.getBuyPrice() * p.getQuantity());
            totalItems += p.getQuantity();
        }

        Locale localeID = new Locale("id", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        tvTotal.setText(formatRupiah.format(estimatedCost));
    }

    private void handleProcurement() {
        if (procurementList.isEmpty()) return;

        transactionController.processProcurement(procurementList, storeId, estimatedCost, result -> {
            if (result.status == Result.Status.SUCCESS) {
                Toast.makeText(this, "Restock Successful!", Toast.LENGTH_LONG).show();
                procurementList.clear();
                updateUI();
                loadProducts(); // Refresh to see updated stock numbers
            } else {
                Toast.makeText(this, "Error: " + result.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProducts() {
        productController.getAll(storeId, result -> {
            if (result.status == Result.Status.SUCCESS) {
                adapter.setProducts(result.data);
            }
        });
    }

    private void setupNavigation(){
        SetupNavigationFacade navFacade = new SetupNavigationFacade(this, bottomNav);
        navFacade.setupNavigation(R.id.nav_stock);
    }
}