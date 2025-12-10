package com.example.beesness.views;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beesness.R;
import com.example.beesness.models.Transaction;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class TransactionDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);

        // 1. Get Data from Intent
        Transaction transaction = (Transaction) getIntent().getSerializableExtra("TRANSACTION_DATA");

        // 2. Init Views
        TextView tvId = findViewById(R.id.tvDetailId);
        TextView tvDate = findViewById(R.id.tvDetailDate);
        TextView tvItems = findViewById(R.id.tvDetailItems);
        TextView tvTotal = findViewById(R.id.tvDetailTotal);
        Button btnClose = findViewById(R.id.btnClose);

        // 3. Populate Data
        if (transaction != null) {
            tvId.setText(transaction.getId());

            if (transaction.getDate() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US);
                tvDate.setText(sdf.format(transaction.getDate()));
            }

            // Show items (replace commas with new lines for better list view)
            if (transaction.getSummary() != null) {
                tvItems.setText(transaction.getSummary().replace(", ", "\n"));
            }

            // Format Money
            Locale localeID = new Locale("id", "ID");
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
            String price = formatRupiah.format(transaction.getTotalAmount());

            if ("SALE".equals(transaction.getType())) {
                tvTotal.setText("+ " + price);
                tvTotal.setTextColor(Color.parseColor("#4CAF50")); // Green
            } else {
                tvTotal.setText("- " + price);
                tvTotal.setTextColor(Color.parseColor("#F44336")); // Red
            }
        }

        btnClose.setOnClickListener(v -> finish());
    }
}