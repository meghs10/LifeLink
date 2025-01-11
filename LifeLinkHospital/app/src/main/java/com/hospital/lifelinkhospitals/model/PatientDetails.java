package com.hospital.lifelinkhospitals.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PatientDetails {
    @SerializedName("id")
    private String id;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("age")
    private int age;

    @SerializedName("gender")
    private Gender gender;

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
    private BloodType bloodType;

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

    // Getters
    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public int getAge() {
        return age;
    }

    public Gender getGender() {
        return gender;
    }

    public List<EmergencyContactResponse> getEmergencyContacts() {
        return emergencyContacts;
    }

    public List<String> getMedicalHistory() {
        return medicalHistory;
    }

    public List<PastSurgeryResponse> getPastSurgeries() {
        return pastSurgeries;
    }

    public List<MedicationResponse> getCurrentMedications() {
        return currentMedications;
    }

    public List<String> getAllergies() {
        return allergies;
    }

    public BloodType getBloodType() {
        return bloodType;
    }

    public double getWeight() {
        return weight;
    }

    public double getHeight() {
        return height;
    }

    public List<String> getDietaryRestrictions() {
        return dietaryRestrictions;
    }

    public boolean isOrganDonor() {
        return organDonor;
    }

    public List<String> getCulturalConsiderations() {
        return culturalConsiderations;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setEmergencyContacts(List<EmergencyContactResponse> emergencyContacts) {
        this.emergencyContacts = emergencyContacts;
    }

    public void setMedicalHistory(List<String> medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public void setPastSurgeries(List<PastSurgeryResponse> pastSurgeries) {
        this.pastSurgeries = pastSurgeries;
    }

    public void setCurrentMedications(List<MedicationResponse> currentMedications) {
        this.currentMedications = currentMedications;
    }

    public void setAllergies(List<String> allergies) {
        this.allergies = allergies;
    }

    public void setBloodType(BloodType bloodType) {
        this.bloodType = bloodType;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setDietaryRestrictions(List<String> dietaryRestrictions) {
        this.dietaryRestrictions = dietaryRestrictions;
    }

    public void setOrganDonor(boolean organDonor) {
        this.organDonor = organDonor;
    }

    public void setCulturalConsiderations(List<String> culturalConsiderations) {
        this.culturalConsiderations = culturalConsiderations;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setLastUpdatedAt(String lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    // Optional: Add a toString() method for debugging
    @Override
    public String toString() {
        return "PatientResponse{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", age=" + age +
                ", gender=" + gender +
                ", bloodType=" + bloodType +
                ", weight=" + weight +
                ", height=" + height +
                ", organDonor=" + organDonor +
                '}';
    }
}
