package com.hospital.lifelinkhospitals;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.hospital.lifelinkhospitals.R;
import com.hospital.lifelinkhospitals.Util.SessionManager;
import com.hospital.lifelinkhospitals.api.ApiService;
import com.hospital.lifelinkhospitals.api.RetrofitClient;
import com.hospital.lifelinkhospitals.model.ApiResponse;
import com.hospital.lifelinkhospitals.model.Hospital;
import com.hospital.lifelinkhospitals.model.HospitalRegistrationRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HospitalRegistrationActivity extends AppCompatActivity {

    private TextInputEditText hospitalNameInput;
    private AutoCompleteTextView hospitalTypeInput;
    private TextInputEditText licenseNumberInput;
    private TextInputEditText yearEstablishedInput;
    private TextInputEditText phoneNumberInput;
    private TextInputEditText addressInput;
    private TextInputEditText cityInput;
    private TextInputEditText stateInput;
    private TextInputEditText pinCodeInput;
    private TextInputEditText totalBedsInput;
    private TextInputEditText icuBedsInput;
    private TextInputEditText emergencyBedsInput;
    private SwitchMaterial ambulanceSwitch;
    private SwitchMaterial emergencySwitch;
    private MaterialCheckBox emergencyDeptCheck;
    private MaterialCheckBox icuCheck;
    private MaterialCheckBox surgeryCheck;
    private MaterialButton submitButton;
    private LinearProgressIndicator progressIndicator;

    private FusedLocationProviderClient fusedLocationClient;
    private double latitude = 0.0;
    private double longitude = 0.0;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_registration);

        initializeViews();
        setupHospitalTypeDropdown();
        setupLocationClient();
        setupSubmitButton();
    }

    private void initializeViews() {
        hospitalNameInput = findViewById(R.id.hospitalNameInput);
        hospitalTypeInput = findViewById(R.id.hospitalTypeInput);
        licenseNumberInput = findViewById(R.id.licenseNumberInput);
        yearEstablishedInput = findViewById(R.id.yearEstablishedInput);
        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        addressInput = findViewById(R.id.addressInput);
        cityInput = findViewById(R.id.cityInput);
        stateInput = findViewById(R.id.stateInput);
        pinCodeInput = findViewById(R.id.pinCodeInput);
        totalBedsInput = findViewById(R.id.totalBedsInput);
        icuBedsInput = findViewById(R.id.icuBedsInput);
        emergencyBedsInput = findViewById(R.id.emergencyBedsInput);
        ambulanceSwitch = findViewById(R.id.ambulanceSwitch);
        emergencySwitch = findViewById(R.id.emergencySwitch);
        emergencyDeptCheck = findViewById(R.id.emergencyDeptCheck);
        icuCheck = findViewById(R.id.icuCheck);
        surgeryCheck = findViewById(R.id.surgeryCheck);
        submitButton = findViewById(R.id.submitButton);
        progressIndicator = findViewById(R.id.progressIndicator);
    }

    private void setupHospitalTypeDropdown() {
        String[] types = new String[]{"Government", "Private", "Specialty"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.dropdown_item,
                types
        );
        hospitalTypeInput.setAdapter(adapter);
    }

    private void setupLocationClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1000);
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    });
        }
    }

    private void setupSubmitButton() {
        submitButton.setOnClickListener(v -> {
            if (validateInputs()) {
                submitRegistration();
            }
        });
    }

    private boolean validateInputs() {
        if (isEmpty(hospitalNameInput)) {
            showError("Hospital name is required");
            return false;
        }
        if (isEmpty(licenseNumberInput)) {
            showError("License number is required");
            return false;
        }
        if (isEmpty(phoneNumberInput)) {
            showError("Phone number is required");
            return false;
        }
        if (isEmpty(addressInput)) {
            showError("Address is required");
            return false;
        }
        if (isEmpty(cityInput)) {
            showError("City is required");
            return false;
        }
        if (isEmpty(stateInput)) {
            showError("State is required");
            return false;
        }
        if (isEmpty(pinCodeInput)) {
            showError("PIN code is required");
            return false;
        }
        if (isEmpty(totalBedsInput) || isEmpty(icuBedsInput) || isEmpty(emergencyBedsInput)) {
            showError("All bed counts are required");
            return false;
        }

        return true;
    }

    private boolean isEmpty(TextInputEditText input) {
        return input.getText() == null || input.getText().toString().trim().isEmpty();
    }

    private void submitRegistration() {
        showLoading(true);

        try {
            HospitalRegistrationRequest request = new HospitalRegistrationRequest.Builder()
                    .hospitalName(hospitalNameInput.getText().toString().trim())
                    .hospitalType(hospitalTypeInput.getText().toString().trim())
                    .licenseNumber(licenseNumberInput.getText().toString().trim())
                    .yearEstablished(yearEstablishedInput.getText().toString().trim())
                    .phoneNumber(phoneNumberInput.getText().toString().trim())
                    .address(addressInput.getText().toString().trim())
                    .city(cityInput.getText().toString().trim())
                    .state(stateInput.getText().toString().trim())
                    .pinCode(pinCodeInput.getText().toString().trim())
                    .location(latitude, longitude)
                    .beds(
                            Integer.parseInt(totalBedsInput.getText().toString().trim()),
                            Integer.parseInt(icuBedsInput.getText().toString().trim()),
                            Integer.parseInt(emergencyBedsInput.getText().toString().trim())
                    )
                    .services(ambulanceSwitch.isChecked(), emergencySwitch.isChecked())
                    .departments(getDepartments())
                    .build();

            String token = new SessionManager(this).getToken();
            if (token == null || token.isEmpty()) {
                showError("Please login first");
                return;
            }

            // Add "Bearer " prefix to token
            String authToken = "Bearer " + token;

            RetrofitClient.getInstance()
                    .getApiService()
                    .registerHospital(authToken, request)
                    .enqueue(new Callback<ApiResponse<Hospital>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Hospital>> call,
                                               Response<ApiResponse<Hospital>> response) {
                            showLoading(false);
                            if (response.isSuccessful() && response.body() != null) {
                                handleSuccessfulRegistration(response.body().getData());
                            } else {
                                if (response.code() == 401) {
                                    new SessionManager(HospitalRegistrationActivity.this).logout();
                                } else {
                                    handleRegistrationError("Registration failed");
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<Hospital>> call, Throwable t) {
                            showLoading(false);
                            handleRegistrationError(t.getMessage());
                        }
                    });

        } catch (NumberFormatException e) {
            showLoading(false);
            showError("Please enter valid numbers for beds");
        } catch (Exception e) {
            showLoading(false);
            showError("Error creating registration: " + e.getMessage());
        }
    }

    private List<String> getDepartments() {
        List<String> departments = new ArrayList<>();
        if (emergencyDeptCheck.isChecked()) departments.add("Emergency");
        if (icuCheck.isChecked()) departments.add("ICU");
        if (surgeryCheck.isChecked()) departments.add("Surgery");
        return departments;
    }

    private void handleSuccessfulRegistration(Hospital hospital) {
        Toast.makeText(this, "Hospital registered successfully!", Toast.LENGTH_LONG).show();
        // You might want to start a new activity or finish this one
        finish();
    }

    private void handleRegistrationError(String message) {
        showError("Registration failed: " + message);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showLoading(boolean show) {
        progressIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        submitButton.setEnabled(!show);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }
}