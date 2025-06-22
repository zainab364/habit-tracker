package com.s23010615.habitease.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.s23010615.habitease.R;
import com.s23010615.habitease.utils.BottomNavHelper;

public class SettingsActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize views
        SwitchMaterial switchNotifications = findViewById(R.id.switchNotifications);
        MaterialButton btnSetHomeLocation = findViewById(R.id.btnSetHomeLocation);
        MaterialButton btnLogout = findViewById(R.id.btnLogout);
        bottomNav = findViewById(R.id.bottomNav);

        // Set up notification switch (load saved preference)
        boolean notificationsEnabled = loadNotificationPreference();
        switchNotifications.setChecked(notificationsEnabled);

        // Handle notification preference changes
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveNotificationPreference(isChecked);
            Toast.makeText(this,
                    isChecked ? "Notifications enabled" : "Notifications disabled",
                    Toast.LENGTH_SHORT).show();
        });

        // Set home location button click
        btnSetHomeLocation.setOnClickListener(v -> {
            startActivity(new Intent(this, SetLocationActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Logout button click
        btnLogout.setOnClickListener(v -> {
            // Perform logout operations
            performLogout();
        });

        // Bottom navigation setup
        bottomNav.setSelectedItemId(R.id.nav_settings);  // Highlight the current tab
        BottomNavHelper.setupBottomNav(bottomNav, this); // Setup navigation
    }

    private boolean loadNotificationPreference() {
        // TODO: Load from SharedPreferences
        return true; // Default to enabled
    }

    private void saveNotificationPreference(boolean enabled) {
        // TODO: Save to SharedPreferences
    }

    private void performLogout() {
        // Clear user session
        // TODO: Implement your logout logic

        // Navigate to LoginActivity and clear back stack
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh UI when returning from other activities
        bottomNav.setSelectedItemId(R.id.nav_settings);
    }
}