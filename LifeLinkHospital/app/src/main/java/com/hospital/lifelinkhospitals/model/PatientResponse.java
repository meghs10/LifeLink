package com.hospital.lifelinkhospitals.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PatientResponse {
    @SerializedName("id")
    private String id;
    
    @SerializedName("fullName")
    private String fullName;
    
    @SerializedName("age")
    private int age;
    
    @SerializedName("gender")
    private String gender;
    
    @SerializedName("emergencyContacts")
    private List<EmergencyContactResponse> emergencyContacts;
    
    @SerializedName("medicalHistory")
    private List<String> medicalHistory;
    
    @SerializedName("pastSurgeries")
    private List<PastSurgeryResponse> pastSurgeries;
    
    @SerializedName("currentMedications")
    private List<MedicationResponse> currentMedications;
    
    @SerializedName("allergies")
    private List<String> allergies;
    
    @SerializedName("bloodType")
    private String bloodType;
    
    @SerializedName("weight")
    private double weight;
    
    @SerializedName("height")
    private double height;
    
    @SerializedName("dietaryRestrictions")
    private List<String> dietaryRestrictions;
    
    @SerializedName("organDonor")
    private boolean organDonor;
    
    @SerializedName("culturalConsiderations")
    private List<String> culturalConsiderations;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("lastUpdatedAt")
    private String lastUpdatedAt;
    
    @SerializedName("insurance")
    private InsuranceResponse insurance;

    // Add getters for all fields
    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public List<EmergencyContactResponse> getEmergencyContacts() { return emergencyContacts; }
    public List<String> getMedicalHistory() { return medicalHistory; }
    public List<PastSurgeryResponse> getPastSurgeries() { return pastSurgeries; }
    public List<MedicationResponse> getCurrentMedications() { return currentMedications; }
    public List<String> getAllergies() { return allergies; }
    public String getBloodType() { return bloodType; }
    public double getWeight() { return weight; }
    public double getHeight() { return height; }
    public List<String> getDietaryRestrictions() { return dietaryRestrictions; }
    public boolean isOrganDonor() { return organDonor; }
    public List<String> getCulturalConsiderations() { return culturalConsiderations; }
    public String getCreatedAt() { return createdAt; }
    public String getLastUpdatedAt() { return lastUpdatedAt; }
    public InsuranceResponse getInsurance() { return insurance; }
    public void setInsurance(InsuranceResponse insurance) { this.insurance = insurance; }
} 