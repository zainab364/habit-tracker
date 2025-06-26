package com.s23010615.habitease.receivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.s23010615.habitease.R;
import com.s23010615.habitease.database.DBHelper;
import com.s23010615.habitease.utils.LocationUtils;

public class HabitReminderReceiver extends BroadcastReceiver {
    private static final String TAG = "HabitReminder";
    public static final float HOME_RADIUS_METERS = 100; // 100 meters radius

    @Override
    public void onReceive(Context context, Intent intent) {
        String habitName = intent.getStringExtra("habitName");
        boolean isHomeOnly = intent.getBooleanExtra("isHomeOnly", false);
        int habitId = intent.getIntExtra("habitId", 0);

        Log.d(TAG, "Received reminder for: " + habitName +
                " (HomeOnly: " + isHomeOnly + ")");

        if (!isHomeOnly) {
            // For regular habits - show notification immediately
            showNotification(context, habitName, habitId);
            return;
        }

        // For home-only habits - check current location
        checkLocationAndNotify(context, habitName, habitId);
    }

    private void checkLocationAndNotify(Context context, String habitName, int habitId) {
        DBHelper dbHelper = new DBHelper(context);
        LatLng homeLocation = dbHelper.getSavedHomeLocation();
        dbHelper.close();

        if (homeLocation == null) {
            Log.d(TAG, "No home location saved");
            return;
        }

        if (!LocationUtils.hasLocationPermission(context)) {
            Log.w(TAG, "Location permission not granted");
            return;
        }

        FusedLocationProviderClient fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(context);

        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            float[] results = new float[1];
                            Location.distanceBetween(
                                    location.getLatitude(), location.getLongitude(),
                                    homeLocation.latitude, homeLocation.longitude,
                                    results
                            );

                            Log.d(TAG, "Distance from home: " + results[0] + " meters");

                            if (results[0] <= HOME_RADIUS_METERS) {
                                showNotification(context, habitName, habitId);
                            } else {
                                Log.d(TAG, "Not showing notification - not at home");
                            }
                        } else {
                            Log.d(TAG, "No location available - can't verify home location");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Location error", e);
                    });
        } catch (SecurityException e) {
            Log.e(TAG, "Location permission exception", e);
        }
    }

    private void showNotification(Context context, String habitName, int habitId) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "habit_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Habit Reminders",
                    NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.bell_solid)
                .setContentTitle("Habit Reminder")
                .setContentText("Time to do: " + habitName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        manager.notify(habitId, builder.build());
        Log.d(TAG, "Notification shown for: " + habitName);
    }
}