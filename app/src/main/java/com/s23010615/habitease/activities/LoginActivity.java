package com.s23010615.habitease.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.Button;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.s23010615.habitease.R;
import com.s23010615.habitease.database.DBHelper;

import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {

    EditText usernameEditText, passwordEditText;
    Button loginButton;
    ImageView fingerprintIcon;
    TextView signUpText;


    DBHelper dbHelper;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        fingerprintIcon = findViewById(R.id.fingerprintIcon);
        signUpText = findViewById(R.id.signUpText);

        dbHelper = new DBHelper(this);

        //  login
        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            } else {
                if (dbHelper.validateCredentials(username,password)) {
                    Toast.makeText( this, "Login successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, HomeActivity.class));

                } else {
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Go to Sign Up screen
        signUpText.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });

        // Setup Biometric
        Executor executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(LoginActivity.this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        Toast.makeText(getApplicationContext(), "Fingerprint login successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(getApplicationContext(), "Fingerprint not recognized", Toast.LENGTH_SHORT).show();
                    }
                });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login with Fingerprint")
                .setDescription("Use your fingerprint to log in")
                .setNegativeButtonText("Cancel")
                .build();

        // Fingerprint click
        fingerprintIcon.setOnClickListener(v -> {
            BiometricManager biometricManager = BiometricManager.from(this);
            int canAuthenticate = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);

            switch (canAuthenticate) {
                case BiometricManager.BIOMETRIC_SUCCESS:
                    biometricPrompt.authenticate(promptInfo); // Show fingerprint dialog
                    break;
                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                    Toast.makeText(this, "No fingerprint sensor detected", Toast.LENGTH_SHORT).show();
                    break;
                case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                    showBiometricEnrollmentDialog();
                    break;
                default:
                    Toast.makeText(this, "Unknown biometric error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showBiometricEnrollmentDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Fingerprint Not Set Up")
                .setMessage("To use fingerprint login, you need to register at least one fingerprint.")
                .setPositiveButton("Register Now", (dialog, which) -> {
                    // Open device settings directly
                    openBiometricEnrollmentSettings();

                })
                .setNegativeButton("Use Password", (dialog, which) -> {
                    // Fallback to password login
                    usernameEditText.requestFocus();
                })
                .setNeutralButton("Remind Me Later", null)
                .show();
    }

    private void openBiometricEnrollmentSettings() {
        try {
            Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
            enrollIntent.putExtra(
                    Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                    BiometricManager.Authenticators.BIOMETRIC_STRONG
            );
            startActivity(enrollIntent);
        } catch (Exception e) {
            // Fallback for devices without biometric enrollment intent
            Toast.makeText(this,
                    "Go to: Settings > Security > Fingerprint",
                    Toast.LENGTH_LONG).show();
        }
    }
}