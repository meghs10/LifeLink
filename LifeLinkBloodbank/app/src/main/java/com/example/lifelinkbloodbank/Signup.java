package com.example.lifelinkbloodbank;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.lifelinkbloodbank.api.RetrofitClient;
import com.example.lifelinkbloodbank.model.MessageResponse;
import com.example.lifelinkbloodbank.model.SignupRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Signup extends AppCompatActivity {

    private TextInputEditText fullNameInput;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private TextInputEditText confirmPasswordInput;
    private MaterialButton signupButton;
    private TextView loginText;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Set status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blood_red));

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        fullNameInput = findViewById(R.id.fullNameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        signupButton = findViewById(R.id.signupButton);
        loginText = findViewById(R.id.loginText);
        progressBar = findViewById(R.id.progressBar);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sign Up");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupClickListeners() {
        signupButton.setOnClickListener(v -> {
            if (validateInputs()) {
                performSignup();
            }
        });

        loginText.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        String fullName = fullNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // Validate full name
        if (fullName.isEmpty()) {
            fullNameInput.setError("Full name is required");
            isValid = false;
        }

        // Validate email
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Please enter a valid email");
            isValid = false;
        }

        // Validate password
        if (password.isEmpty() || password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            isValid = false;
        }

        // Validate confirm password
        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            isValid = false;
        }

        return isValid;
    }

    private void performSignup() {
        String name = fullNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        showLoading();

        SignupRequest signupRequest = new SignupRequest(name, email, password, "BLOOD_BANK");

        RetrofitClient.getInstance()
                .getApiService()
                .signup(signupRequest)
                .enqueue(new Callback<MessageResponse>() {
                    @Override
                    public void onResponse(Call<MessageResponse> call,
                                           Response<MessageResponse> response) {
                        hideLoading();

                        if (response.isSuccessful() && response.body() != null) {
                            showSuccessDialog("Registration successful! Please login.");
                        } else {
                            handleErrorResponse(response);
                        }
                    }

                    @Override
                    public void onFailure(Call<MessageResponse> call, Throwable t) {
                        hideLoading();
                        Log.e(TAG, "Signup failed", t);
                        showError("Network error. Please try again.");
                    }
                });
    }

    private void handleErrorResponse(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                Log.e(TAG, "Signup error response: " + errorBody);

                try {
                    // First try to parse as MessageResponse
                    Gson gson = new Gson();
                    MessageResponse errorResponse = gson.fromJson(errorBody, MessageResponse.class);

                    if (errorResponse != null && errorResponse.getMessage() != null) {
                        // Handle specific error messages
                        String errorMessage = errorResponse.getMessage();
                        if (errorMessage.contains("Email already exists")) {
                            showError("This email is already registered. Please login or use a different email.");
                        } else if (errorMessage.contains("Invalid email format")) {
                            emailInput.setError("Please enter a valid email address");
                            showError("Invalid email format");
                        } else if (errorMessage.contains("Password too weak")) {
                            passwordInput.setError("Password must be at least 6 characters");
                            showError("Password is too weak. Please use at least 6 characters.");
                        } else {
                            showError(errorMessage);
                        }
                    } else {
                        showError("Registration failed. Please try again.");
                    }
                } catch (JsonSyntaxException e) {
                    Log.e(TAG, "Error parsing error response", e);

                    // Try to parse as a different error format if your API uses one
                    try {
                        JSONObject jsonError = new JSONObject(errorBody);
                        String message = jsonError.optString("message",
                                "Registration failed. Please try again.");
                        showError(message);
                    } catch (JSONException jsonException) {
                        showError("An unexpected error occurred. Please try again.");
                    }
                }
            } else {
                // Handle case where errorBody is null
                if (response.code() == 409) {
                    showError("This email is already registered. Please login or use a different email.");
                } else if (response.code() == 400) {
                    showError("Invalid input. Please check your details and try again.");
                } else {
                    showError("Registration failed. Please try again.");
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error reading error response", e);
            showError("An unexpected error occurred. Please try again.");
        }
    }

    private void showSuccessDialog(String message) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Success")
                .setMessage(message)
                .setPositiveButton("Login", (dialog, which) -> {
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                })
                .setCancelable(false)
                .show();
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        signupButton.setEnabled(false);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        signupButton.setEnabled(true);
    }

    private void showError(String message) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
