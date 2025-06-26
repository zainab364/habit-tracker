package com.s23010615.habitease.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import com.google.android.gms.maps.model.LatLng;

public class LocationUtils {

    public static boolean hasLocationPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}
//    public static final float HOME_RADIUS_METERS = 100; // 100 meters radius
//
//    @SuppressLint("MissingPermission")
//    public static boolean isUserAtHome(Context context, LatLng homeLatLng) {
//        if (homeLatLng == null) return false;
//
//        // Check location permissions
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Log.w("LocationUtils", "Location permission not granted");
//            return false;
//        }
//
//        try {
//            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//
//            // Try to get the most recent location
//            Location lastKnown = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            if (lastKnown == null) {
//                lastKnown = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//            }
//
//            if (lastKnown == null) {
//                Log.w("LocationUtils", "No last known location available");
//                return false;
//            }
//
//            float[] result = new float[1];
//            Location.distanceBetween(
//                    lastKnown.getLatitude(), lastKnown.getLongitude(),
//                    homeLatLng.latitude, homeLatLng.longitude,
//                    result
//            );
//
//            Log.d("LocationUtils", "Distance from home: " + result[0] + " meters");
//            return result[0] <= HOME_RADIUS_METERS;
//        } catch (Exception e) {
//            Log.e("LocationUtils", "Error checking location", e);
//            return false;
//        }
//    }




