package com.s23010615.habitease.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.s23010615.habitease.utils.BottomNavHelper;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.s23010615.habitease.R;
import com.s23010615.habitease.adapters.HabitAdapter;
import com.s23010615.habitease.database.DBHelper;
import com.s23010615.habitease.models.Habit;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity {
    private static final int ADD_HABIT_REQUEST = 1;
    private RecyclerView habitRecyclerView;
    private FloatingActionButton addHabitButton;
    private DBHelper dbHelper;
    private HabitAdapter habitAdapter;
    private ImageView notificationIcon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d("LIFECYCLE", "HomeActivity created");

        dbHelper = new DBHelper(this);
        // Setup RecyclerView
        habitRecyclerView = findViewById(R.id.habitRecyclerView);
        habitRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadHabits(); // Load habits

        List<Habit> habits = dbHelper.getAllHabits();
        habitAdapter = new HabitAdapter(this, habits);
        habitRecyclerView.setAdapter(habitAdapter);
        loadHabits();

        // Setup FAB
        addHabitButton = findViewById(R.id.addHabitButton);
        addHabitButton.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, AddHabitActivity.class);
            startActivityForResult(intent, ADD_HABIT_REQUEST);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Setup notification
        notificationIcon = findViewById(R.id.notificationIcon);
        notificationIcon.setOnClickListener(view -> {
            startActivity(new Intent(this, NotificationActivity.class));        });

// Temporary Data for Habit list for test
        if (habits.isEmpty()) {
            dbHelper.addHabit(new Habit("Test Habit", "07:00 AM", true));
            habits = dbHelper.getAllHabits(); // reload
            habitAdapter = new HabitAdapter(this, habits);
            habitRecyclerView.setAdapter(habitAdapter);
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        // Highlight the current tab
        bottomNav.setSelectedItemId(R.id.nav_home);
        // Setup navigation
        BottomNavHelper.setupBottomNav(bottomNav, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_HABIT_REQUEST && resultCode == RESULT_OK) {
            loadHabits(); // Refresh the list when returning from AddHabitActivity
            Toast.makeText(this, "Habit added successfully", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadHabits() {
        new Thread(()  -> {
            List<Habit> habits = dbHelper.getAllHabits();
            runOnUiThread(() -> {
                if (habitAdapter == null) {
                    habitAdapter = new HabitAdapter(HomeActivity.this, habits);
                    habitRecyclerView.setAdapter(habitAdapter);
                } else {
                    habitAdapter.updateHabits(habits);
                }
            });
        }).start();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
        Log.d("LIFECYCLE", "HomeActivity destroyed");
    }
}
