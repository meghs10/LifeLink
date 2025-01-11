package com.example.lifelinkbloodbank.model;


import com.google.gson.annotations.SerializedName;

public class BloodRequest {
    @SerializedName("id")
    private String id;

    @SerializedName("hospitalId")
    private String hospitalId;

    @SerializedName("patientId")
    private String patientId;

    @SerializedName("bloodType")
    private BloodType bloodType;

    @SerializedName("status")
    private String status;

    public BloodRequest(String hospitalId, String patientId, BloodType bloodType, String status) {
        this.hospitalId = hospitalId;
        this.patientId = patientId;
        this.bloodType = bloodType;
        this.status = status;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public String getPatientId() {
        return patientId;
    }

    public BloodType getBloodType() {
        return bloodType;
    }

    public String getStatus() {
        return status;
    }

    // Setter for status updates
    public void setStatus(String status) {
        this.status = status;
    }
}