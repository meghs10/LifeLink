package com.example.lifelinkbloodbank.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Hospital {
    @SerializedName("id")
    private String id;

    @SerializedName("userId")
    private String userId;

    @SerializedName("hospitalName")
    private String hospitalName;

    @SerializedName("hospitalType")
    private String hospitalType;

    @SerializedName("licenseNumber")
    private String licenseNumber;

    @SerializedName("yearEstablished")
    private String yearEstablished;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("address")
    private String address;

    @SerializedName("city")
    private String city;

    @SerializedName("state")
    private String state;

    @SerializedName("pinCode")
    private String pinCode;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("totalBeds")
    private int totalBeds;

    @SerializedName("icuBeds")
    private int icuBeds;

    @SerializedName("emergencyBeds")
    private int emergencyBeds;

    @SerializedName("hasAmbulanceService")
    private boolean hasAmbulanceService;

    @SerializedName("hasEmergencyService")
    private boolean hasEmergencyService;

    @SerializedName("departments")
    private List<String> departments;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    // Getters
    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public String getHospitalType() {
        return hospitalType;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public String getYearEstablished() {
        return yearEstablished;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPinCode() {
        return pinCode;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getTotalBeds() {
        return totalBeds;
    }

    public int getIcuBeds() {
        return icuBeds;
    }

    public int getEmergencyBeds() {
        return emergencyBeds;
    }

    public boolean hasAmbulanceService() {
        return hasAmbulanceService;
    }

    public boolean hasEmergencyService() {
        return hasEmergencyService;
    }

    public List<String> getDepartments() {
        return departments;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    // Helper methods
    public String getFullAddress() {
        return String.format("%s, %s, %s - %s", address, city, state, pinCode);
    }

    public int getAvailableBeds() {
        return totalBeds - (icuBeds + emergencyBeds);
    }

    @Override
    public String toString() {
        return hospitalName;
    }
}
