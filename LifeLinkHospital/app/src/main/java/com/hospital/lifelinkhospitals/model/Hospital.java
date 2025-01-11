package com.hospital.lifelinkhospitals.model;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Hospital {
    private String id;
    private String userId;
    private String hospitalName;
    private String hospitalType;
    private String licenseNumber;
    private String yearEstablished;
    private String phoneNumber;
    private String address;
    private String city;
    private String state;
    private String pinCode;
    private double latitude;
    private double longitude;
    private int totalBeds;
    private int icuBeds;
    private int emergencyBeds;
    private boolean hasAmbulanceService;
    private boolean hasEmergencyService;
    private List<String> departments;
    private String createdAt;
    private String updatedAt;

    public String getId(){
        return this.id;
    }

    public String getHospitalName(){
        return this.hospitalName;
    }

    public String getAddress(){
        return this.address;
    }

    public int getTotalBeds(){
        return this.totalBeds;
    }
}