package com.hospital.lifelinkhospitals.model;

import com.google.gson.annotations.SerializedName;

public class BloodRequest {
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

    // Getters and Setters
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

}
