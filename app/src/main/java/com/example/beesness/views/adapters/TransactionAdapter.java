package com.example.beesness.views.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beesness.R;
import com.example.beesness.models.Transaction;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private List<Transaction> list = new ArrayList<>();

    // 1. CLICK LISTENER INTERFACE
    public interface OnItemClickListener {
        void onItemClick(Transaction transaction);
    }
    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setTransactions(List<Transaction> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(v, this); // Pass 'this' adapter
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction t = list.get(position);

        String shortId = t.getId().length() > 8 ? t.getId().substring(0, 8).toUpperCase() : t.getId();
        holder.tvId.setText("#" + shortId);

        if (t.getDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, HH:mm", Locale.US);
            holder.tvDate.setText(sdf.format(t.getDate()));
        }

        Locale localeID = new Locale("id", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        String price = formatRupiah.format(t.getTotalAmount());

        if ("SALE".equals(t.getType())) {
            holder.tvType.setText("SALE");
            holder.tvType.setTextColor(Color.parseColor("#4CAF50")); // Green Text

            holder.tvAmount.setText("+ " + price);
            holder.tvAmount.setTextColor(Color.parseColor("#4CAF50")); // Green Price
        } else {
            holder.tvType.setText("RESTOCK");
            holder.tvType.setTextColor(Color.parseColor("#F44336")); // Red Text

            holder.tvAmount.setText("- " + price);
            holder.tvAmount.setTextColor(Color.parseColor("#F44336")); // Red Price
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvType, tvDate, tvAmount;

        public ViewHolder(@NonNull View itemView, TransactionAdapter adapter) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvTransId);
            tvType = itemView.findViewById(R.id.tvType);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);

            itemView.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && adapter.listener != null) {
                    adapter.listener.onItemClick(adapter.list.get(pos));
                }
            });
        }
    }
}