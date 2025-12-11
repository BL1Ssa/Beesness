package com.example.beesness.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beesness.R;
import com.example.beesness.controller.ProductController;
import com.example.beesness.models.Product;
import com.example.beesness.models.ProductCategory;
import com.example.beesness.utils.Result;
import com.example.beesness.utils.SessionManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AddProductActivity extends AppCompatActivity {

    private TextView tvStock;
    private EditText etName, etBuyPrice, etSellPrice, etStock, etDesc;
    private Spinner spinnerCategory;
    private ImageView ivProductImage;
    private Button btnSave, btnCancel;

    // private Button btnDelete;

    private ProductController productController;
    private String storeId;
    private Uri selectedImageUri = null;

    // === EDIT MODE VARIABLES ===
    private Product productToEdit;
    private boolean isEditMode = false;
    private SessionManager sessionManager;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    ivProductImage.setImageURI(selectedImageUri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        sessionManager = new SessionManager(this);
        storeId = sessionManager.getCurrentStoreId();

        productController = new ProductController();

        if (getIntent().hasExtra("PRODUCT_TO_EDIT")) {
            productToEdit = (Product) getIntent().getSerializableExtra("PRODUCT_TO_EDIT");
            isEditMode = true;
        }

        initViews();
        setupSpinner();

        if (isEditMode) {
            setupEditMode();
        }

        ivProductImage.setOnClickListener(v -> openGallery());
        btnSave.setOnClickListener(v -> saveOrUpdateProduct());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void initViews() {
        etName = findViewById(R.id.etProductName);
        etBuyPrice = findViewById(R.id.etBuyPrice);
        etSellPrice = findViewById(R.id.etSellPrice);
        etStock = findViewById(R.id.etStock);
        etDesc = findViewById(R.id.etDescription);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        ivProductImage = findViewById(R.id.ivProductImage);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void setupEditMode() {
        TextView title = findViewById(R.id.tvTitle);
        if(title != null) title.setText("Edit Product");
        btnSave.setText("Update Product");
        tvStock = findViewById(R.id.tvStock);
        tvStock.setText("Current Stock");

        // Fill Fields
        etName.setText(productToEdit.getName());
        etBuyPrice.setText(String.valueOf((long)productToEdit.getBuyPrice()));
        etSellPrice.setText(String.valueOf((long)productToEdit.getSellPrice()));
        etStock.setText(String.valueOf(productToEdit.getQuantity()));
        etDesc.setText(productToEdit.getDescription());

        if (productToEdit.getImage() != null && !productToEdit.getImage().isEmpty()) {
            Picasso.get().load(productToEdit.getImage())
                    .fit().centerCrop().into(ivProductImage);
        }

    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void setupSpinner() {
        List<ProductCategory> categories = new ArrayList<>();
        categories.add(new ProductCategory("CAT01", "Beverages", "BEV"));
        categories.add(new ProductCategory("CAT02", "Food", "FOO"));
        categories.add(new ProductCategory("CAT03", "Snacks", "SNK"));
        ArrayAdapter<ProductCategory> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(adapter);
    }

    private void saveOrUpdateProduct() {
        String name = etName.getText().toString();
        String buyPrice = etBuyPrice.getText().toString();
        String sellPrice = etSellPrice.getText().toString();
        String stock = etStock.getText().toString();
        String desc = etDesc.getText().toString();
        ProductCategory selectedCategory = (ProductCategory) spinnerCategory.getSelectedItem();

        String imageToUse = "";

        if (selectedImageUri != null) {
            imageToUse = selectedImageUri.toString();
        } else if (isEditMode) {
            imageToUse = productToEdit.getImage();
        }

        btnSave.setEnabled(false);
        btnSave.setEnabled(false);
        btnSave.setClickable(false);
        btnCancel.setClickable(false);
        btnSave.setText("Adding Product...");
        if (isEditMode) {
            // === UPDATE EXISTING ===
            productController.update(
                    productToEdit.getId(),
                    storeId, name, buyPrice, sellPrice, desc, stock,
                    selectedCategory, imageToUse,
                    result -> {
                        btnSave.setEnabled(true);
                        if (result.status == Result.Status.SUCCESS) {
                            Toast.makeText(this, "Product Updated!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else if(result.status == Result.Status.ERROR){
                            Toast.makeText(this, "Update Failed: " + result.message, Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        } else {
            productController.add(
                    storeId, name, buyPrice, sellPrice, desc, stock,
                    selectedCategory, imageToUse,
                    result -> {
                        btnSave.setEnabled(true);
                        if (result.status == Result.Status.SUCCESS) {
                            Toast.makeText(this, "Product Added!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else if (result.status == Result.Status.ERROR){
                            Toast.makeText(this, "Error: " + result.message, Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
    }
}