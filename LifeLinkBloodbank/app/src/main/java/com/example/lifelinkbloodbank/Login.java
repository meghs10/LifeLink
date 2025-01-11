package com.example.lifelinkbloodbank;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lifelinkbloodbank.api.RetrofitClient;
import com.example.lifelinkbloodbank.model.JwtResponse;
import com.example.lifelinkbloodbank.model.LoginRequest;
import com.example.lifelinkbloodbank.model.MessageResponse;
import com.example.lifelinkbloodbank.util.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private MaterialButton loginButton;
    private TextView signupText;

    private SessionManager sessionManager;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();

        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            navigateToMain();
            finish();
            return;
        }
        setupClickListeners();
    }

    private void initializeViews() {
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        signupText = findViewById(R.id.signupText);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(v -> {
            if (validateInputs()) {
                performLogin();
            }
        });

        signupText.setOnClickListener(v -> {
            Intent intent = new Intent(this, Signup.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Please enter a valid email");
            isValid = false;
        }

        if (password.isEmpty() || password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            isValid = false;
        }

        return isValid;
    }

    private void performLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        showLoading();

        LoginRequest loginRequest = new LoginRequest(email, password);

        // Add logging
        Log.d(TAG, "Attempting login for email: " + email);

        RetrofitClient.getInstance()
                .getApiService()
                .login(loginRequest)
                .enqueue(new Callback<JwtResponse>() {
                    @Override
                    public void onResponse(Call<JwtResponse> call, Response<JwtResponse> response) {
                        hideLoading();

                        // Add response logging
                        Log.d(TAG, "Login response code: " + response.code());

                        if (response.isSuccessful() && response.body() != null) {
                            JwtResponse jwtResponse = response.body();
                            Log.d(TAG, "Login successful. Token received.");

                            try {
                                sessionManager.saveAuthResponse(jwtResponse);
                                Log.d(TAG, "Auth response saved to session");
                                navigateToMain();
                            } catch (Exception e) {
                                Log.e(TAG, "Error saving auth response", e);
                                showError("Error processing login response");
                            }
                        } else {
                            Log.e(TAG, "Login failed with code: " + response.code());
                            handleErrorResponse(response);
                        }
                    }

                    @Override
                    public void onFailure(Call<JwtResponse> call, Throwable t) {
                        hideLoading();
                        Log.e(TAG, "Login network call failed", t);
                        showError("Network error: " + t.getMessage());
                    }
                });
    }

    private void handleErrorResponse(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                Log.e(TAG, "Error response body: " + errorBody);

                try {
                    Gson gson = new Gson();
                    MessageResponse errorResponse = gson.fromJson(errorBody, MessageResponse.class);
                    String errorMessage = errorResponse != null && errorResponse.getMessage() != null ?
                            errorResponse.getMessage() : "Authentication failed";
                    showError(errorMessage);
                } catch (JsonSyntaxException e) {
                    Log.e(TAG, "Error parsing error response", e);
                    showError("Authentication failed. Please check your credentials.");
                }
            } else {
                showError("Authentication failed. Please try again.");
            }
        } catch (IOException e) {
            Log.e(TAG, "Error reading error response", e);
            showError("An unexpected error occurred. Please try again.");
        }
    }

    private void showError(final String message) {
        runOnUiThread(() -> {
            try {
                new MaterialAlertDialogBuilder(Login.this)
                        .setTitle("Error")
                        .setMessage(message)
                        .setPositiveButton("OK", null)
                        .setBackground(getResources().getDrawable(R.drawable.dialog_rounded_bg))
                        .show();
            } catch (Exception e) {
                Log.e(TAG, "Error showing error dialog", e);
                // Fallback to Toast if dialog fails
                Toast.makeText(Login.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);
        emailInput.setEnabled(false);
        passwordInput.setEnabled(false);
        signupText.setEnabled(false);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        loginButton.setEnabled(true);
        emailInput.setEnabled(true);
        passwordInput.setEnabled(true);
        signupText.setEnabled(true);
    }





    private void navigateToMain() {
        Intent intent;
        intent = new Intent(Login.this, BloodBankDashboard.class);
        startActivity(intent);
        finish();
    }
}