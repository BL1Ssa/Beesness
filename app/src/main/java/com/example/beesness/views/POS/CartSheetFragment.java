package com.example.beesness.views.POS;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beesness.R;
import com.example.beesness.models.Product;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class CartSheetFragment extends BottomSheetDialogFragment {

    private List<Product> cartList;
    private CartUpdateListener listener;

    public interface CartUpdateListener {
        void onCartUpdated();
        Boolean onStockRestored(String productId, int quantityRestored);
    }

    public CartSheetFragment(List<Product> cartList, CartUpdateListener listener) {
        this.cartList = cartList;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart_sheet, container, false);

        RecyclerView rv = view.findViewById(R.id.rvCartItems);
        Button btnClose = view.findViewById(R.id.btnCloseCart);

        // Pass the listener to the adapter
        CartAdapter adapter = new CartAdapter(cartList, listener);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        btnClose.setOnClickListener(v -> dismiss());

        return view;
    }

    private static class CartAdapter extends RecyclerView.Adapter<CartAdapter.VH> {
        List<Product> list;
        CartUpdateListener listener;

        public CartAdapter(List<Product> list, CartUpdateListener listener) {
            this.list = list;
            this.listener = listener;
        }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_edit, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            Product p = list.get(position);
            holder.tvName.setText(p.getName());
            holder.tvQty.setText(String.valueOf(p.getQuantity()));

            // PLUS BUTTON
            holder.btnPlus.setOnClickListener(v -> {
                if(!listener.onStockRestored(p.getId(), -1)) return;
                p.setQuantity(p.getQuantity() + 1);
                notifyItemChanged(position);

                listener.onCartUpdated();

            });

            // MINUS BUTTON
            holder.btnMinus.setOnClickListener(v -> {
                if (p.getQuantity() > 1) {
                    p.setQuantity(p.getQuantity() - 1);
                    notifyItemChanged(position);

                    // 2. CRITICAL: Tell Activity to put 1 back on shelf
                    listener.onStockRestored(p.getId(), 1);
                    listener.onCartUpdated();
                }else if(p.getQuantity() == 1){
                    deleteFromCart(position, p);
                }
            });

            // DELETE BUTTON
            holder.btnDel.setOnClickListener(v -> {
                deleteFromCart(position, p);
            });
        }

        private void deleteFromCart(int position, Product p) {
            int qtyToRestore = p.getQuantity(); // Save quantity before deleting
            list.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, list.size());

            //This is to restore stock in the main list
            listener.onStockRestored(p.getId(), qtyToRestore);
            listener.onCartUpdated();
        }

        @Override public int getItemCount() { return list.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvName, tvQty;
            Button btnPlus, btnMinus;
            ImageButton btnDel;
            public VH(View v) {
                super(v);
                tvName = v.findViewById(R.id.tvCartName);
                tvQty = v.findViewById(R.id.tvCartQty);
                btnPlus = v.findViewById(R.id.btnCartPlus);
                btnMinus = v.findViewById(R.id.btnCartMinus);
                btnDel = v.findViewById(R.id.btnCartDel);
            }
        }
    }
}