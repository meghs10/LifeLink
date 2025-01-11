package com.hospital.lifelinkhospitals;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hospital.lifelinkhospitals.adapters.EmergencyContactsAdapter;
import com.hospital.lifelinkhospitals.adapters.MedicationsAdapter;
import com.hospital.lifelinkhospitals.adapters.PastSurgeriesAdapter;
import com.hospital.lifelinkhospitals.api.RetrofitClient;
import com.hospital.lifelinkhospitals.model.PatientResponse;
import com.hospital.lifelinkhospitals.model.InsuranceResponse;
import com.hospital.lifelinkhospitals.Util.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class PatientDetailsActivity extends AppCompatActivity {
    private TextView patientName, patientAge, patientGender, patientBloodType;
    private TextView allergiesText, dietaryRestrictionsText;
    private TextView organDonorText, culturalConsiderationsText;
    private TextView weightText, heightText;
    private TextView insuranceProviderText, policyNumberText, groupNumberText, policyHolderText, relationshipText, startDateText, endDateText;
    private TextView insuranceTypeText, planTypeText, emergencyServiceText, ambulanceServiceText;

    private RecyclerView emergencyContactsRecyclerView;
    private RecyclerView medicationsRecyclerView;
    private RecyclerView pastSurgeriesRecyclerView;

    private EmergencyContactsAdapter emergencyContactsAdapter;
    private MedicationsAdapter medicationsAdapter;
    private PastSurgeriesAdapter pastSurgeriesAdapter;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details);

        initializeViews();

        String userId = getIntent().getStringExtra("userId");
        if (userId == null) {
            finish();
            return;
        }

        String token = sessionManager.getToken();
        if (token == null) {
            finish();
            return;
        }

        loadPatientDetails(userId, "Bearer " + token);
        loadInsuranceDetails(userId, "Bearer " + token);
    }

    private void initializeViews() {
        sessionManager = new SessionManager(this);

        // Initialize TextViews
        patientName = findViewById(R.id.patientName);
        patientAge = findViewById(R.id.patientAge);
        patientGender = findViewById(R.id.patientGender);
        patientBloodType = findViewById(R.id.patientBloodType);
        allergiesText = findViewById(R.id.allergiesText);
        dietaryRestrictionsText = findViewById(R.id.dietaryRestrictionsText);
        organDonorText = findViewById(R.id.organDonorText);
        culturalConsiderationsText = findViewById(R.id.culturalConsiderationsText);
        weightText = findViewById(R.id.weightText);
        heightText = findViewById(R.id.heightText);
        insuranceProviderText = findViewById(R.id.insuranceProviderText);
        policyNumberText = findViewById(R.id.policyNumberText);
        groupNumberText = findViewById(R.id.groupNumberText);
        policyHolderText = findViewById(R.id.policyHolderText);
        relationshipText = findViewById(R.id.relationshipText);
        startDateText = findViewById(R.id.startDateText);
        endDateText = findViewById(R.id.endDateText);
        insuranceTypeText = findViewById(R.id.insuranceTypeText);
        planTypeText = findViewById(R.id.planTypeText);
        emergencyServiceText = findViewById(R.id.emergencyServiceText);
        ambulanceServiceText = findViewById(R.id.ambulanceServiceText);

        // Initialize RecyclerViews
        medicationsRecyclerView = findViewById(R.id.medicationsRecyclerView);
        medicationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        medicationsAdapter = new MedicationsAdapter();
        medicationsRecyclerView.setAdapter(medicationsAdapter);

        pastSurgeriesRecyclerView = findViewById(R.id.pastSurgeriesRecyclerView);
        pastSurgeriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        pastSurgeriesAdapter = new PastSurgeriesAdapter();
        pastSurgeriesRecyclerView.setAdapter(pastSurgeriesAdapter);

        emergencyContactsRecyclerView = findViewById(R.id.emergencyContactsRecyclerView);
        emergencyContactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        emergencyContactsAdapter = new EmergencyContactsAdapter();
        emergencyContactsRecyclerView.setAdapter(emergencyContactsAdapter);
    }

    private void loadPatientDetails(String userId, String authToken) {
        showLoading();

        RetrofitClient.getInstance()
                .getApiService()
                .getPatientDetailsByUserId(authToken, userId)
                .enqueue(new Callback<PatientResponse>() {
                    @Override
                    public void onResponse(Call<PatientResponse> call, Response<PatientResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            updateUI(response.body());
                        } else {

                        }
                    }

                    @Override
                    public void onFailure(Call<PatientResponse> call, Throwable t) {

                    }
                });
    }

    private void loadInsuranceDetails(String userId, String authToken) {
        RetrofitClient.getInstance()
                .getApiService()
                .getInsurancesByUserId(authToken, userId)
                .enqueue(new Callback<List<InsuranceResponse>>() {
                    @Override
                    public void onResponse(Call<List<InsuranceResponse>> call, 
                                        Response<List<InsuranceResponse>> response) {
                        if (response.isSuccessful() && response.body() != null && 
                            !response.body().isEmpty()) {
                            updateInsuranceUI(response.body().get(0)); // Display first insurance
                        }
                    }

                    @Override
                    public void onFailure(Call<List<InsuranceResponse>> call, Throwable t) {

                    }
                });
    }

    private void updateUI(PatientResponse patient) {
        if (patient == null) return;

        // Hide loading indicator
        findViewById(R.id.loadingIndicator).setVisibility(View.GONE);
        findViewById(R.id.mainContent).setVisibility(View.VISIBLE);

        // Update basic information
        patientName.setText(patient.getFullName());
        patientAge.setText(String.format("%d years", patient.getAge()));
        patientGender.setText(patient.getGender());
        patientBloodType.setText(String.format("Blood Type: %s", patient.getBloodType()));

        // Update physical measurements
        weightText.setText(String.format("Weight: %.1f kg", patient.getWeight()));
        heightText.setText(String.format("Height: %.1f cm", patient.getHeight()));

        // Update medical information
        if (patient.getAllergies() != null && !patient.getAllergies().isEmpty()) {
            allergiesText.setText("Allergies:\n• " + TextUtils.join("\n• ", patient.getAllergies()));
        } else {
            allergiesText.setText("No known allergies");
        }

        if (patient.getDietaryRestrictions() != null && !patient.getDietaryRestrictions().isEmpty()) {
            dietaryRestrictionsText.setText("Dietary Restrictions:\n• " +
                    TextUtils.join("\n• ", patient.getDietaryRestrictions()));
        } else {
            dietaryRestrictionsText.setText("No dietary restrictions");
        }

        organDonorText.setText("Organ Donor: " + (patient.isOrganDonor() ? "Yes" : "No"));

        if (patient.getCulturalConsiderations() != null && !patient.getCulturalConsiderations().isEmpty()) {
            culturalConsiderationsText.setText("Cultural Considerations:\n• " +
                    TextUtils.join("\n• ", patient.getCulturalConsiderations()));
        } else {
            culturalConsiderationsText.setText("No cultural considerations specified");
        }


        // Update RecyclerViews
        if (patient.getCurrentMedications() != null) {
            medicationsAdapter.setMedications(patient.getCurrentMedications());
        }

        if (patient.getPastSurgeries() != null) {
            pastSurgeriesAdapter.setSurgeries(patient.getPastSurgeries());
        }

        if (patient.getEmergencyContacts() != null) {
            emergencyContactsAdapter.setContacts(patient.getEmergencyContacts());
        }
    }

    private void updateInsuranceUI(InsuranceResponse insurance) {
        if (insurance == null) return;

        insuranceProviderText.setText("Provider: " + insurance.getInsuranceProviderName());
        policyNumberText.setText("Policy #: " + insurance.getPolicyNumber());
        groupNumberText.setText("Group #: " + insurance.getGroupNumber());
        policyHolderText.setText("Policy Holder: " + insurance.getPolicyHolderName());
        relationshipText.setText("Relationship: " + insurance.getRelationshipToPolicyHolder());
        insuranceTypeText.setText("Type: " + insurance.getInsuranceType());
        planTypeText.setText("Plan: " + insurance.getPlanType());
        
        startDateText.setText("Start Date: " + insurance.getStartDate());
        endDateText.setText("End Date: " + insurance.getEndDate());
        
        String emergencyService = insurance.isCoversEmergencyService() ? "Covered" : "Not Covered";
        String ambulanceService = insurance.isCoversAmbulanceService() ? "Covered" : "Not Covered";
        
        emergencyServiceText.setText("Emergency Services: " + emergencyService);
        ambulanceServiceText.setText("Ambulance Services: " + ambulanceService);
    }

    private void showLoading() {
        findViewById(R.id.loadingIndicator).setVisibility(View.VISIBLE);
        findViewById(R.id.mainContent).setVisibility(View.GONE);
    }
}