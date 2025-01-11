package com.hospital.lifelinkhospitals.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IncomingPatient {
    @SerializedName("id")
    private String id;

    @SerializedName("userId")
    private String userId;

    @SerializedName("hospitalId")
    private String hospitalId;

    private PatientResponse patientDetails;
    private List<InsuranceResponse> insurances;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() {
        return userId;
    }

    public PatientResponse getPatientDetails() {
        return patientDetails;
    }

    public void setPatientDetails(PatientResponse patientDetails) {
        this.patientDetails = patientDetails;
    }

    public List<InsuranceResponse> getInsurances() {
        return insurances;
    }

    public void setInsurances(List<InsuranceResponse> insurances) {
        this.insurances = insurances;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }
}
