package com.example.lifelinkbloodbank;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.lifelinkbloodbank.api.RetrofitClient;
import com.example.lifelinkbloodbank.model.BloodRequest;
import com.example.lifelinkbloodbank.model.BloodRequestUI;
import com.example.lifelinkbloodbank.model.Hospital;
import com.example.lifelinkbloodbank.model.RequestStatusUpdate;
import com.example.lifelinkbloodbank.util.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BloodBankDashboard extends AppCompatActivity implements BloodRequestAdapter.OnRequestActionListener {

    private RecyclerView requestsRecyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private View emptyStateView;
    private View loadingView;
    private BloodRequestAdapter adapter;
    private SessionManager sessionManager;
    private final Map<String, Hospital> hospitalCache = new HashMap<>();
    private static final String TAG = "BloodBankDashboard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_bank_dashboard);
        setupViews();
        setupServices();
        // Initial load
        loadRequests();
    }

    private void setupViews() {
        requestsRecyclerView = findViewById(R.id.requestsRecyclerView);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        emptyStateView = findViewById(R.id.emptyState);
        loadingView = findViewById(R.id.loadingView);

        adapter = new BloodRequestAdapter(this);
        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        requestsRecyclerView.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(() -> {
            showToast("Refreshing requests...");
            loadRequests();
        });
    }

    private void setupServices() {
        sessionManager = new SessionManager(this);
    }

    private void loadRequests() {
        showLoading(true);
        String token = "Bearer " + sessionManager.getToken();
        String bloodBankId = sessionManager.getUserId();

        if (token == null || bloodBankId == null) {
            showToast("Authentication error");
            finishLoading();
            return;
        }

        RetrofitClient.getInstance()
                .getApiService()
                .getBloodRequests(token, bloodBankId)
                .enqueue(new Callback<List<BloodRequest>>() {
                    @Override
                    public void onResponse(Call<List<BloodRequest>> call,
                                           Response<List<BloodRequest>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            processBloodRequests(response.body());
                        } else {
                            showToast("Failed to load requests: " + response.code());
                            finishLoading();
                            showEmptyState(true);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<BloodRequest>> call, Throwable t) {
                        showToast("Network error: " + t.getMessage());
                        finishLoading();
                        showEmptyState(true);
                    }
                });
    }

    private void processBloodRequests(List<BloodRequest> requests) {
        if (requests.isEmpty()) {
            showEmptyState(true);
            finishLoading();
            return;
        }

        List<BloodRequestUI> uiRequests = new ArrayList<>();
        AtomicInteger pendingRequests = new AtomicInteger(requests.size());

        for (BloodRequest request : requests) {
            // Check cache first
            if (hospitalCache.containsKey(request.getHospitalId())) {
                Hospital hospital = hospitalCache.get(request.getHospitalId());
                uiRequests.add(createUIRequest(request, hospital));
                if (pendingRequests.decrementAndGet() == 0) {
                    updateAdapter(uiRequests);
                }
            } else {
                // Fetch hospital details if not in cache
                fetchHospitalDetails(request, new OnHospitalFetchedListener() {
                    @Override
                    public void onHospitalFetched(Hospital hospital) {
                        if (hospital != null) {
                            uiRequests.add(createUIRequest(request, hospital));
                        } else {
                            // Create UI request with placeholder data
                            BloodRequestUI uiRequest = new BloodRequestUI(request);
                            uiRequest.setHospitalName("Hospital Details Unavailable");
                            uiRequest.setAddress("Address Unavailable");
                            uiRequest.setContactNumber("Contact Unavailable");
                            uiRequests.add(uiRequest);
                        }

                        if (pendingRequests.decrementAndGet() == 0) {
                            updateAdapter(uiRequests);
                        }
                    }
                });
            }
        }
    }

    private void updateAdapter(List<BloodRequestUI> requests) {
        runOnUiThread(() -> {
            adapter.submitList(new ArrayList<>(requests));
            showEmptyState(requests.isEmpty());
            finishLoading();
        });
    }

    private void fetchHospitalDetails(BloodRequest request, OnHospitalFetchedListener listener) {
        String token = "Bearer " + sessionManager.getToken();

        RetrofitClient.getInstance()
                .getApiService()
                .getHospitalDetails(token, request.getHospitalId())
                .enqueue(new Callback<Hospital>() {
                    @Override
                    public void onResponse(Call<Hospital> call, Response<Hospital> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Hospital hospital = response.body();
                            hospitalCache.put(hospital.getId(), hospital);
                            listener.onHospitalFetched(hospital);
                            showToast("Fetched details for: " + hospital.getHospitalName());
                        } else {
                            showToast("Failed to fetch hospital details" + response.code());
                            listener.onHospitalFetched(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<Hospital> call, Throwable t) {
                        showToast("Network error: " + t.getMessage());
                        listener.onHospitalFetched(null);
                    }
                });
    }

    private void finishLoading() {
        showLoading(false);
        swipeRefresh.setRefreshing(false);
    }

    // Your existing helper methods
    private void showLoading(boolean show) {
        loadingView.setVisibility(show ? View.VISIBLE : View.GONE);
        swipeRefresh.setEnabled(!show);
    }

    private void showEmptyState(boolean show) {
        emptyStateView.setVisibility(show ? View.VISIBLE : View.GONE);
        requestsRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showToast(String message) {
    }

    // Request status update methods
    @Override
    public void onAcceptRequest(BloodRequestUI request) {
        updateRequestStatus(request, "ACCEPTED");
    }

    @Override
    public void onRejectRequest(BloodRequestUI request) {
        updateRequestStatus(request, "REJECTED");
    }

    private void updateRequestStatus(BloodRequestUI request, String newStatus) {
        String token = "Bearer " + sessionManager.getToken();
        RequestStatusUpdate statusUpdate = new RequestStatusUpdate(newStatus);

        RetrofitClient.getInstance()
                .getApiService()
                .updateRequestStatus(token, request.getId(), statusUpdate)
                .enqueue(new Callback<BloodRequest>() {
                    @Override
                    public void onResponse(Call<BloodRequest> call, Response<BloodRequest> response) {
                        if (response.isSuccessful()) {
                            showToast("Request " + newStatus.toLowerCase());
                            loadRequests();
                        } else {
                            String error = response.code() == 404 ? "Request not found" :
                                    response.code() == 400 ? "Invalid status update" :
                                            "Failed to update request";
                            showToast(error);
                        }
                    }

                    @Override
                    public void onFailure(Call<BloodRequest> call, Throwable t) {
                        showToast("Network error: " + t.getMessage());
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRequests();
    }

    private BloodRequestUI createUIRequest(BloodRequest request, Hospital hospital) {
        BloodRequestUI uiRequest = new BloodRequestUI(request);
        if (hospital != null) {
            uiRequest.setHospitalName(hospital.getHospitalName());
            uiRequest.setAddress(hospital.getFullAddress());
            uiRequest.setContactNumber(hospital.getPhoneNumber());
        } else {
            uiRequest.setHospitalName("Hospital Details Unavailable");
            uiRequest.setAddress("Address Unavailable");
            uiRequest.setContactNumber("Phone Unavailable");
        }
        return uiRequest;
    }

    private interface OnHospitalFetchedListener {
        void onHospitalFetched(Hospital hospital);
    }
}