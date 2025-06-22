package com.s23010615.habitease.utils;
// shared utility method helps avoid repeating the same bottom navigation code in every activity

import android.app.Activity;
import android.content.Intent;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.s23010615.habitease.R;
import com.s23010615.habitease.activities.HomeActivity;
import com.s23010615.habitease.activities.ProgressActivity;
import com.s23010615.habitease.activities.SettingsActivity;

public class BottomNavHelper {

    public static void setupBottomNav(BottomNavigationView bottomNav, Activity currentActivity) {
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home && !currentActivity.getClass().equals(HomeActivity.class)) {
                currentActivity.startActivity(new Intent(currentActivity, HomeActivity.class));
                currentActivity.overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_stats && !currentActivity.getClass().equals(ProgressActivity.class)) {
                currentActivity.startActivity(new Intent(currentActivity, ProgressActivity.class));
                currentActivity.overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_settings && !currentActivity.getClass().equals(SettingsActivity.class)) {
                currentActivity.startActivity(new Intent(currentActivity, SettingsActivity.class));
                currentActivity.overridePendingTransition(0, 0);
                return true;
            }

            return true; // return true to keep the selected state
        });
    }
}

