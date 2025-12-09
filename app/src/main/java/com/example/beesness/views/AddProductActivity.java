package com.example.beesness.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beesness.R;
import com.example.beesness.controller.ProductController;
import com.example.beesness.models.ProductCategory;
import com.example.beesness.utils.Result;

import java.util.ArrayList;
import java.util.List;

public class AddProductActivity extends AppCompatActivity {

    private EditText etName, etBuyPrice, etSellPrice, etStock, etDesc;
    private Spinner spinnerCategory;
    private ImageView ivProductImage;
    private Button btnSave, btnCancel;

    private ProductController productController;
    private String storeId;

    private Uri selectedImageUri = null;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    // Show the selected image immediately
                    ivProductImage.setImageURI(selectedImageUri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        productController = new ProductController();
        storeId = getIntent().getStringExtra("STORE_ID");

        initViews();
        setupSpinner();

        ivProductImage.setOnClickListener(v -> openGallery());

        btnSave.setOnClickListener(v -> saveProduct());
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

    private void saveProduct() {
        btnSave.setEnabled(false);
        btnSave.setText("Saving...");

        String name = etName.getText().toString();
        String buyPrice = etBuyPrice.getText().toString();
        String sellPrice = etSellPrice.getText().toString();
        String stock = etStock.getText().toString();
        String desc = etDesc.getText().toString();
        ProductCategory selectedCategory = (ProductCategory) spinnerCategory.getSelectedItem();

        // Check if image is selected
        String imageString = "";
        if (selectedImageUri != null) {
            imageString = selectedImageUri.toString();
        } else {
            // Optional: Handle case where no image is picked (maybe use a default string)
            imageString = "";
        }

        productController.add(
                storeId,
                name,
                buyPrice,
                sellPrice,
                desc,
                stock,
                selectedCategory,
                imageString,
                result -> {
                    if (result.status == Result.Status.SUCCESS) {
                        Toast.makeText(this, "Product Added!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (result.status == Result.Status.ERROR) {
                        btnSave.setEnabled(true);
                        btnSave.setText("Add Product");
                        Toast.makeText(this, "Error: " + result.message, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}