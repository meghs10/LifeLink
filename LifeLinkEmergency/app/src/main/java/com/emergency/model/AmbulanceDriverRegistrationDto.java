package com.emergency.model;


import com.google.gson.annotations.SerializedName;

public class AmbulanceDriverRegistrationDto {

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("dateOfBirth")
    private String dateOfBirth;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("currentAddress")
    private String currentAddress;

    @SerializedName("driversLicenseNumber")
    private String driversLicenseNumber;

    @SerializedName("licenseType")
    private String licenseType;

    @SerializedName("experienceWithEmergencyVehicle")
    private boolean experienceWithEmergencyVehicle;

    @SerializedName("yearsOfEmergencyExperience")
    private Integer yearsOfEmergencyExperience;

    @SerializedName("vehicleRegistrationNumber")
    private String vehicleRegistrationNumber;

    @SerializedName("hasAirConditioning")
    private boolean hasAirConditioning;

    @SerializedName("hasOxygenCylinderHolder")
    private boolean hasOxygenCylinderHolder;

    @SerializedName("hasStretcher")
    private boolean hasStretcher;

    @SerializedName("insurancePolicyNumber")
    private String insurancePolicyNumber;

    @SerializedName("insuranceExpiryDate")
    private String insuranceExpiryDate;

    public AmbulanceDriverRegistrationDto() {
    }

    // Getters
    public String getFullName() {
        return fullName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public String getDriversLicenseNumber() {
        return driversLicenseNumber;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public boolean isExperienceWithEmergencyVehicle() {
        return experienceWithEmergencyVehicle;
    }

    public Integer getYearsOfEmergencyExperience() {
        return yearsOfEmergencyExperience;
    }

    public String getVehicleRegistrationNumber() {
        return vehicleRegistrationNumber;
    }

    public boolean isHasAirConditioning() {
        return hasAirConditioning;
    }

    public boolean isHasOxygenCylinderHolder() {
        return hasOxygenCylinderHolder;
    }

    public boolean isHasStretcher() {
        return hasStretcher;
    }

    public String getInsurancePolicyNumber() {
        return insurancePolicyNumber;
    }

    public String getInsuranceExpiryDate() {
        return insuranceExpiryDate;
    }

    // Setters
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public void setDriversLicenseNumber(String driversLicenseNumber) {
        this.driversLicenseNumber = driversLicenseNumber;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public void setExperienceWithEmergencyVehicle(boolean experienceWithEmergencyVehicle) {
        this.experienceWithEmergencyVehicle = experienceWithEmergencyVehicle;
    }

    public void setYearsOfEmergencyExperience(Integer yearsOfEmergencyExperience) {
        this.yearsOfEmergencyExperience = yearsOfEmergencyExperience;
    }

    public void setVehicleRegistrationNumber(String vehicleRegistrationNumber) {
        this.vehicleRegistrationNumber = vehicleRegistrationNumber;
    }

    public void setHasAirConditioning(boolean hasAirConditioning) {
        this.hasAirConditioning = hasAirConditioning;
    }

    public void setHasOxygenCylinderHolder(boolean hasOxygenCylinderHolder) {
        this.hasOxygenCylinderHolder = hasOxygenCylinderHolder;
    }

    public void setHasStretcher(boolean hasStretcher) {
        this.hasStretcher = hasStretcher;
    }

    public void setInsurancePolicyNumber(String insurancePolicyNumber) {
        this.insurancePolicyNumber = insurancePolicyNumber;
    }

    public void setInsuranceExpiryDate(String insuranceExpiryDate) {
        this.insuranceExpiryDate = insuranceExpiryDate;
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "AmbulanceDriverRegistrationDto{" +
                "fullName='" + fullName + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", currentAddress='" + currentAddress + '\'' +
                ", driversLicenseNumber='" + driversLicenseNumber + '\'' +
                ", licenseType='" + licenseType + '\'' +
                ", experienceWithEmergencyVehicle=" + experienceWithEmergencyVehicle +
                ", yearsOfEmergencyExperience=" + yearsOfEmergencyExperience +
                ", vehicleRegistrationNumber='" + vehicleRegistrationNumber + '\'' +
                ", hasAirConditioning=" + hasAirConditioning +
                ", hasOxygenCylinderHolder=" + hasOxygenCylinderHolder +
                ", hasStretcher=" + hasStretcher +
                ", insurancePolicyNumber='" + insurancePolicyNumber + '\'' +
                ", insuranceExpiryDate='" + insuranceExpiryDate + '\'' +
                '}';
    }
}
