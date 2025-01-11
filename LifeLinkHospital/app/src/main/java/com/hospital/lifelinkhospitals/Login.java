package com.hospital.lifelinkhospitals;

import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.hospital.lifelinkhospitals.Util.DialogUtils;
import com.hospital.lifelinkhospitals.Util.MessageUtils;
import com.hospital.lifelinkhospitals.Util.SessionManager;
import com.hospital.lifelinkhospitals.api.RetrofitClient;
import com.hospital.lifelinkhospitals.model.JwtResponse;
import com.hospital.lifelinkhospitals.model.LoginRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private MaterialButton loginButton;
    private View signupText;
    private SessionManager sessionManager;
    private View rootView;

    private TextView statusMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        rootView = findViewById(android.R.id.content);

        sessionManager = new SessionManager(this);
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        signupText = findViewById(R.id.signupText);
        statusMessage = findViewById(R.id.statusMessage);
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(v -> attemptLogin());
        signupText.setOnClickListener(v -> {
            startActivity(new Intent(Login.this, Signup.class));
        });
    }

    private void attemptLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            MessageUtils.showError(statusMessage, "Please fill in all fields");
            return;
        }

        // Show loading state
        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");
        MessageUtils.hideMessage(statusMessage);

        LoginRequest request = new LoginRequest(email, password);

        RetrofitClient.getInstance()
                .getApiService()
                .login(request)
                .enqueue(new Callback<JwtResponse>() {
                    @Override
                    public void onResponse(Call<JwtResponse> call, Response<JwtResponse> response) {
                        loginButton.setEnabled(true);
                        loginButton.setText("Login");

                        if (response.isSuccessful() && response.body() != null) {
                            JwtResponse jwtResponse = response.body();
                            sessionManager.saveUserSession(
                                    jwtResponse.getToken(),
                                    jwtResponse.getRefreshToken(),
                                    jwtResponse.getEmail(),
                                    jwtResponse.getId(),
                                    jwtResponse.getName(),
                                    jwtResponse.getRole()
                            );

                            MessageUtils.showSuccess(statusMessage, "Login successful!");

                            // Wait for 1 second before navigating
                            new Handler().postDelayed(() -> {
                                startActivity(new Intent(Login.this, Dashboard.class));
                                finish();
                            }, 1000);
                        } else {
                            try {
                                String errorBody = response.errorBody().string();
                                MessageUtils.showError(statusMessage, errorBody);
                            } catch (Exception e) {
                                MessageUtils.showError(statusMessage, "Login failed");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JwtResponse> call, Throwable t) {
                        loginButton.setEnabled(true);
                        loginButton.setText("Login");
                        MessageUtils.showError(statusMessage, "Network error: " + t.getMessage());
                    }
                });
    }
}