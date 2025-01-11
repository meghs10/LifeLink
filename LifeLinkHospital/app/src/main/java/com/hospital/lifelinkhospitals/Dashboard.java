package com.hospital.lifelinkhospitals;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.hospital.lifelinkhospitals.Util.SessionManager;
import com.hospital.lifelinkhospitals.adapters.IncomingPatientsAdapter;
import com.hospital.lifelinkhospitals.api.RetrofitClient;
import com.hospital.lifelinkhospitals.model.Hospital;
import com.hospital.lifelinkhospitals.model.IncomingPatient;
import com.hospital.lifelinkhospitals.model.InsuranceResponse;
import com.hospital.lifelinkhospitals.model.PatientResponse;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class Dashboard extends AppCompatActivity implements IncomingPatientsAdapter.OnPatientClickListener {
    private RecyclerView patientsRecyclerView;
    private IncomingPatientsAdapter patientsAdapter;
    private ProgressBar progressBar;
    private View emptyStateLayout;
    private SwipeRefreshLayout swipeRefresh;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        
        initializeViews();
        sessionManager = new SessionManager(this);

        // First get the hospital ID
        String userId = sessionManager.getUserId(); // Make sure you have this method in SessionManager
        if (userId != null) {
            getHospitalId(userId);
        } else {
            showEmptyState();
        }
    }

    private void initializeViews() {
        // Initialize views
        patientsRecyclerView = findViewById(R.id.patientsRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        // Setup RecyclerView
        patientsAdapter = new IncomingPatientsAdapter(this);
        patientsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        patientsRecyclerView.setAdapter(patientsAdapter);

        // Setup SwipeRefreshLayout
        swipeRefresh.setOnRefreshListener(() -> {
            String hospitalId = sessionManager.getHospitalId();
            if (hospitalId != null) {
                loadIncomingPatients(hospitalId);
            }
        });

    }

    private void getHospitalId(String userId) {
        String token = sessionManager.getToken();
        if (token == null) {
            showEmptyState();
            return;
        }

        String authToken = token.startsWith("Bearer ") ? token : "Bearer " + token;
        showLoading();

        RetrofitClient.getInstance()
                .getApiService()
                .getHospitalId(userId, authToken)
                .enqueue(new Callback<Hospital>() {
                    @Override
                    public void onResponse(Call<Hospital> call, Response<Hospital> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Hospital hospital = response.body();
                            String hospitalId = hospital.getId();

                            
                            // Save hospital ID to session
                            sessionManager.saveHospitalId(hospitalId);
                            
                            // Now load the patients
                            loadIncomingPatients(hospitalId);
                        } else {
                            showEmptyState();
                            finishLoading();
                        }
                    }

                    @Override
                    public void onFailure(Call<Hospital> call, Throwable t) {
                        showEmptyState();
                        finishLoading();
                    }
                });
    }

    private void loadIncomingPatients(String hospitalId) {
        String token = sessionManager.getToken();
        if (token == null) {
            showEmptyState();
            return;
        }

        String authToken = token.startsWith("Bearer ") ? token : "Bearer " + token;


        showLoading();
        RetrofitClient.getInstance()
                .getApiService()
                .getIncomingPatients(authToken, hospitalId)
                .enqueue(new Callback<List<IncomingPatient>>() {
                    @Override
                    public void onResponse(Call<List<IncomingPatient>> call, Response<List<IncomingPatient>> response) {
                        // Print the raw response for debugging
                        if (response.errorBody() != null) {

                        }


                        if (response.isSuccessful() && response.body() != null) {
                            List<IncomingPatient> patients = response.body();
                            
                            if (patients.isEmpty()) {

                                showEmptyState();
                            } else {
                                hideEmptyState();
                                patientsAdapter.setPatients(patients);
                                for (IncomingPatient patient : patients) {
                                    loadPatientDetails(patient, authToken);
                                }
                            }
                        } else {
                            showEmptyState();
                        }
                        finishLoading();
                    }

                    @Override
                    public void onFailure(Call<List<IncomingPatient>> call, Throwable t) {
                        t.printStackTrace(); // Print stack trace to logcat
                        showEmptyState();
                        finishLoading();
                    }
                });
    }

    private void showLoading() {
        runOnUiThread(() -> {
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
            if (patientsRecyclerView != null) {
                patientsRecyclerView.setVisibility(View.GONE);
            }
            if (emptyStateLayout != null) {
                emptyStateLayout.setVisibility(View.GONE);
            }
        });
    }

    private void finishLoading() {
        runOnUiThread(() -> {
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            if (swipeRefresh != null) {
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void showEmptyState() {
        runOnUiThread(() -> {
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            if (emptyStateLayout != null) {
                emptyStateLayout.setVisibility(View.VISIBLE);
            }
            if (patientsRecyclerView != null) {
                patientsRecyclerView.setVisibility(View.GONE);
            }
        });
    }

    private void hideEmptyState() {
        runOnUiThread(() -> {
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            if (emptyStateLayout != null) {
                emptyStateLayout.setVisibility(View.GONE);
            }
            if (patientsRecyclerView != null) {
                patientsRecyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    // Add method to check API endpoint
    private void checkApiEndpoint() {
        String token = sessionManager.getToken();
        String hospitalId = sessionManager.getHospitalId();
        
        if (token != null && hospitalId != null) {
            Call<List<IncomingPatient>> call = RetrofitClient.getInstance()
                    .getApiService()
                    .getIncomingPatients("Bearer " + token, hospitalId);

        }
    }

    @Override
    public void onPatientClick(IncomingPatient patient) {

        Intent intent = new Intent(this, PatientDetailsActivity.class);
        intent.putExtra("userId", patient.getUserId());
        startActivity(intent);
    }

    private void loadPatientDetails(IncomingPatient patient, String authToken) {
        RetrofitClient.getInstance()
                .getApiService()
                .getPatientDetailsByUserId(authToken, patient.getUserId())
                .enqueue(new Callback<PatientResponse>() {
                    @Override
                    public void onResponse(Call<PatientResponse> call, Response<PatientResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            patient.setPatientDetails(response.body());
                            patientsAdapter.notifyDataSetChanged();
                            
                            // Load insurance details after patient details
                            loadPatientInsurance(patient, authToken);
                        } else {

                        }
                    }

                    @Override
                    public void onFailure(Call<PatientResponse> call, Throwable t) {

                    }
                });
    }

    private void loadPatientInsurance(IncomingPatient patient, String authToken) {
        RetrofitClient.getInstance()
                .getApiService()
                .getInsurancesByUserId(authToken, patient.getUserId())
                .enqueue(new Callback<List<InsuranceResponse>>() {
                    @Override
                    public void onResponse(Call<List<InsuranceResponse>> call,
                                         Response<List<InsuranceResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            patient.setInsurances(response.body());
                            patientsAdapter.notifyDataSetChanged();
                        } else {
                        }
                    }

                    @Override
                    public void onFailure(Call<List<InsuranceResponse>> call, Throwable t) {

                    }
                });
    }
}