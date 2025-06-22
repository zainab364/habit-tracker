package com.s23010615.habitease.activities;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.s23010615.habitease.R;
import com.s23010615.habitease.database.DBHelper;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SetLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng selectedLocation;
    private TextInputEditText searchLocation;
    private DBHelper dbHelper;
    private static final float DEFAULT_ZOOM = 15f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_location);

        dbHelper = new DBHelper(this);
        searchLocation = findViewById(R.id.searchLocation);
        MaterialButton btnSave = findViewById(R.id.btnSaveLocation);
        ImageButton btnBack = findViewById(R.id.btnBack);

//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        if (mapFragment != null) {
//            mapFragment.getMapAsync(this);
//        }

        // Handle search location
        findViewById(R.id.searchContainer).setOnClickListener(v -> performSearch());

        // Save selected location
        btnSave.setOnClickListener(v -> {
            if (selectedLocation != null) {
                boolean success = dbHelper.saveHomeLocation(selectedLocation.latitude, selectedLocation.longitude);
                if (success) {
                    Toast.makeText(this, "Location saved!", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to Settings
                } else {
                    Toast.makeText(this, "Failed to save location", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please select a location", Toast.LENGTH_SHORT).show();
            }
        });

        // Back button
        btnBack.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng defaultLatLng = new LatLng(6.9271, 79.8612); // Colombo
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 12));

        // Tap to select location
        mMap.setOnMapClickListener(latLng -> {
            selectedLocation = latLng;
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Home Location"));
        });
    }

    private void performSearch() {
        String locationText = searchLocation.getText() != null ? searchLocation.getText().toString().trim() : "";
        if (!locationText.isEmpty()) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addressList = geocoder.getFromLocationName(locationText, 1);
                if (!addressList.isEmpty()) {
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                } else {
                    Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Geocoder error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

