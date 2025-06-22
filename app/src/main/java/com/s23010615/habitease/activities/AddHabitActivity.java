package com.s23010615.habitease.activities;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// Bottom Nav
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.s23010615.habitease.utils.BottomNavHelper;

import com.google.android.material.button.MaterialButton;
import com.s23010615.habitease.R;
import com.s23010615.habitease.database.DBHelper;
import com.s23010615.habitease.models.Habit;

import java.util.Calendar;
import java.util.Locale;

public class AddHabitActivity extends AppCompatActivity {

    private EditText etHabitName, etHabitTime;
    private SwitchMaterial switchHomeOnly;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);

        // Initialize views
        etHabitName = findViewById(R.id.etHabitName);
        etHabitTime = findViewById(R.id.etHabitTime);
        switchHomeOnly = findViewById(R.id.switchHomeOnly);
        MaterialButton btnSaveHabit = findViewById(R.id.btnSaveHabit);
        dbHelper = new DBHelper(this);

        // Set up time picker
        etHabitTime.setOnClickListener(v -> showTimePicker());

        // Save button click listener
        btnSaveHabit.setOnClickListener(v -> saveHabit());

        // Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        // Setup navigation
        BottomNavHelper.setupBottomNav(bottomNav, this);
    }

    private void showTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    String amPm;
                    int displayHour;

                    if (selectedHour >= 12) {
                        amPm = "PM";
                        displayHour = (selectedHour > 12) ? selectedHour - 12 : selectedHour;
                    } else {
                        amPm = "AM";
                        displayHour = (selectedHour == 0) ? 12 : selectedHour;
                    }

                    String time = String.format(Locale.getDefault(),
                            "%02d:%02d %s",
                            displayHour,
                            selectedMinute,
                            amPm);
                    etHabitTime.setText(time);
                },
                hour,
                minute,
                false // 24-hour format
        );
        timePickerDialog.show();
    }

    private void saveHabit() {
        String name = etHabitName.getText().toString().trim();
        String time = etHabitTime.getText().toString().trim();
        boolean homeOnly = switchHomeOnly.isChecked();

        // Validate inputs
        if (name.isEmpty()) {
            etHabitName.setError("Habit name cannot be empty");
            return;
        }

        if (time.isEmpty()) {
            etHabitTime.setError("Please select a time");
            return;
        }

        // Create and save habit
        Habit habit = new Habit(name, time, homeOnly);
        try {
            long result = dbHelper.addHabit(habit);
            if (result != -1) {
                // Return the new habit data to HomeActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("new_habit_added", true);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Failed to save habit", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error saving habit", Toast.LENGTH_SHORT).show();
            Log.e("DB_ERROR", "Error saving habit", e);
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}