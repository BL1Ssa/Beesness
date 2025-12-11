package com.example.beesness.facade;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.example.beesness.R;
import com.example.beesness.views.MainActivity;
import com.example.beesness.views.POSActivity;
import com.example.beesness.views.StockActivity;
import com.example.beesness.views.TransactionHistoryActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SetupNavigationFacade {

    private final Activity activity;
    private final BottomNavigationView bottomNavigationView;

    public SetupNavigationFacade(Activity activity, BottomNavigationView bottomNavigationView) {
        this.activity = activity;
        this.bottomNavigationView = bottomNavigationView;
    }

    /**
     * Sets up the bottom navigation selection and listener.
     *
     * @param currentMenuId The R.id of the menu item that represents the current screen.
     */
    public void setupNavigation(int currentMenuId) {
        // Set the highlighted item
        bottomNavigationView.setSelectedItemId(currentMenuId);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            // If the user clicks the tab they are already on, do nothing
            if (id == currentMenuId) {
                return true;
            }

            if (id == R.id.nav_home) {
                startActivity(MainActivity.class);
                return true;

            } else if (id == R.id.nav_transaction) {
                startActivity(TransactionHistoryActivity.class);
                return true;

            } else if (id == R.id.nav_cart) {
                startActivity(POSActivity.class);
                return true;

            } else if (id == R.id.nav_stock) {
                startActivity(StockActivity.class);
                return true;

            } else if (id == R.id.nav_profile) {
                Toast.makeText(activity, "Opening Profile...", Toast.LENGTH_SHORT).show();
                return true;
            }

            return false;
        });
    }

    private void startActivity(Class<?> targetActivity) {
        Intent intent = new Intent(activity, targetActivity);
        // Optional: Add flags here if you want to clear back stack or singleTop
        // intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(intent);
        // Optional: activity.overridePendingTransition(0, 0); // Remove animation if desired
    }
}
