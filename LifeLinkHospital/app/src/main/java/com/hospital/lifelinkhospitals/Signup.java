package com.hospital.lifelinkhospitals;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.hospital.lifelinkhospitals.Util.DialogUtils;
import com.hospital.lifelinkhospitals.Util.MessageUtils;
import com.hospital.lifelinkhospitals.Util.SessionManager;
import com.hospital.lifelinkhospitals.api.RetrofitClient;
import com.hospital.lifelinkhospitals.model.MessageResponse;
import com.hospital.lifelinkhospitals.model.SignupRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Signup extends AppCompatActivity {

    private TextInputEditText fullNameInput;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private MaterialButton signupButton;
    private View loginText;
    private SessionManager sessionManager;

    private TextView statusMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        sessionManager = new SessionManager(this);
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        fullNameInput = findViewById(R.id.fullNameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        signupButton = findViewById(R.id.signupButton);
        loginText = findViewById(R.id.loginText);
        statusMessage = findViewById(R.id.statusMessage);
    }

    private void setupClickListeners() {
        signupButton.setOnClickListener(v -> attemptSignup());
        loginText.setOnClickListener(v -> {
            startActivity(new Intent(Signup.this, Login.class));
            finish();
        });
    }

    private void attemptSignup() {
        String fullName = fullNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (!validateInputs(fullName, email, password)) {
            return;
        }

        // Show loading state
        signupButton.setEnabled(false);
        signupButton.setText("Creating Account...");
        MessageUtils.hideMessage(statusMessage);

        SignupRequest request = new SignupRequest(fullName, email, password, "HOSPITAL");

        RetrofitClient.getInstance()
                .getApiService()
                .signup(request)
                .enqueue(new Callback<MessageResponse>() {
                    @Override
                    public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                        signupButton.setEnabled(true);
                        signupButton.setText("Sign Up");

                        if (response.isSuccessful() && response.body() != null) {
                            MessageResponse messageResponse = response.body();
                            MessageUtils.showSuccess(statusMessage, messageResponse.getMessage());

                            // Wait for 2 seconds before navigating
                            new Handler().postDelayed(() -> {
                                startActivity(new Intent(Signup.this, Login.class));
                                finish();
                            }, 2000);
                        } else {
                            try {
                                String errorBody = response.errorBody().string();
                                MessageUtils.showError(statusMessage, errorBody);
                            } catch (Exception e) {
                                MessageUtils.showError(statusMessage, "Signup failed");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MessageResponse> call, Throwable t) {
                        signupButton.setEnabled(true);
                        signupButton.setText("Sign Up");
                        MessageUtils.showError(statusMessage, "Network error: " + t.getMessage());
                    }
                });
    }

    private boolean validateInputs(String fullName, String email, String password) {
        if (fullName.isEmpty()) {
            fullNameInput.setError("Name is required");
            fullNameInput.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            emailInput.setError("Email is required");
            emailInput.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Please enter a valid email");
            emailInput.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            passwordInput.setError("Password is required");
            passwordInput.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            passwordInput.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Signup.this, Login.class));
        finish();
    }
}