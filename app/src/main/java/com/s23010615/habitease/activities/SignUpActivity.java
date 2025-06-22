package com.s23010615.habitease.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.s23010615.habitease.R;
import com.s23010615.habitease.database.DBHelper;

public class SignUpActivity extends AppCompatActivity {

    // UI Components
    private TextInputEditText usernameEditText, passwordEditText, confirmPasswordEditText;
    private MaterialButton signUpButton;
    private TextView loginText;

    // Database Helper
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Database Helper
        dbHelper = new DBHelper(this);

        // Initialize UI Components
        initializeViews();

        // listener for sign up button
        signUpButton.setOnClickListener(v -> validateAndSignUp());

        // Set click listener for login text
        loginText.setOnClickListener(v -> navigateToHome());

    }

private void initializeViews() {
    usernameEditText = findViewById(R.id.usernameEditText);
    passwordEditText = findViewById(R.id.passwordEditText);
    confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
    signUpButton = findViewById(R.id.signUpButton);
    loginText = findViewById(R.id.loginText);
}

private void validateAndSignUp() {
    // Get user input
    String username = usernameEditText.getText().toString().trim();
    String password = passwordEditText.getText().toString().trim();
    String confirmPassword = confirmPasswordEditText.getText().toString().trim();

    // Validate inputs
    if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
        showToast("Please fill in all fields");
        return;
    }

    if (username.length() < 3) {
        showToast("Username must be at least 3 characters");
        return;
    }

    if (password.length() < 6) {
        showToast("Password must be at least 6 characters");
        return;
    }

    if (!password.equals(confirmPassword)) {
        showToast("Passwords do not match");
        return;
    }

    // Check if username already exists
    if (dbHelper.isUsernameTaken(username)) {
        showToast("Username already exists");
        return;
    }

    // Attempt to register user
    boolean isRegistered = dbHelper.insertUser(username, password);

    if (isRegistered) {
        showToast("Registration successful");
        navigateToHome(); // Or navigate to main activity
    } else {
        showToast("Registration failed");
    }
}

private void navigateToHome() {
    Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
    startActivity(intent);
    finish(); // close this activity to prevent going back
}

private void showToast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
}

@Override
protected void onDestroy() {
    dbHelper.close();
    super.onDestroy();
}
}