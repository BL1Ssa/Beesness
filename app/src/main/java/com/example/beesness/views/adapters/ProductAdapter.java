package com.example.beesness.views.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beesness.R;
import com.example.beesness.models.Product;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList = new ArrayList<>();

    public void setProducts(List<Product> products) {
        this.productList = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.nameTv.setText(product.getName());
        holder.categoryTv.setText(product.getProductType());

        Locale localeID = new Locale("id", "ID"); // "id" = Indonesian, "ID" = Indonesia
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        holder.priceTv.setText(formatRupiah.format(product.getSellPrice()));
        holder.stockTv.setText("Qty: " + product.getQuantity());

        if (holder.productImageView == null) return;

        android.util.Log.d("DEBUG_IMAGE", "Product: " + product.getName());
        android.util.Log.d("DEBUG_IMAGE", "Loading URL: " + product.getImage());

        if (product.getImage() != null && !product.getImage().isEmpty()) {
            Picasso.get()
                    .load(product.getImage())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.stat_notify_error)
                    .fit()
                    .centerCrop()
                    .into(holder.productImageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            android.util.Log.d("DEBUG_IMAGE", "Success loading image!");
                        }

                        @Override
                        public void onError(Exception e) {
                            android.util.Log.e("DEBUG_IMAGE", "Picasso Error: " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
        } else {
            android.util.Log.d("DEBUG_IMAGE", "URL was null or empty. Showing default.");
            holder.productImageView.setImageResource(android.R.drawable.ic_menu_gallery);
        }

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView nameTv, categoryTv, priceTv, stockTv;
        ImageView productImageView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.tvProductName);
            categoryTv = itemView.findViewById(R.id.tvProductCategory);
            priceTv = itemView.findViewById(R.id.tvProductPrice);
            stockTv = itemView.findViewById(R.id.tvProductStock);
            productImageView = itemView.findViewById(R.id.ivProductImage);
        }
    }
}