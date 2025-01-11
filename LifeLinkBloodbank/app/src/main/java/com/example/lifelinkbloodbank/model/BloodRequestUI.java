package com.example.lifelinkbloodbank.model;

public class BloodRequestUI {
    private final BloodRequest request;
    private String hospitalName;
    private String address;
    private String contactNumber;

    public BloodRequestUI(BloodRequest request) {
        this.request = request;
    }

    // Getters for the wrapped BloodRequest fields
    public String getId() {
        return request.getId();
    }

    public String getHospitalId() {
        return request.getHospitalId();
    }

    public String getPatientId() {
        return request.getPatientId();
    }

    public BloodType getBloodType() {
        return request.getBloodType();
    }

    public String getStatus() {
        return request.getStatus();
    }

    // Getters and setters for UI-specific fields
    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    // Get the original request if needed
    public BloodRequest getRequest() {
        return request;
    }

    // Helper methods for UI display
    public boolean isPending() {
        return "PENDING".equals(request.getStatus());
    }

    public String getStatusDisplay() {
        String status = request.getStatus();
        if (status == null) return "";
        return status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
    }

    public String getBloodTypeDisplay() {
        return request.getBloodType().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BloodRequestUI that = (BloodRequestUI) o;
        return request.getId().equals(that.request.getId());
    }

    @Override
    public int hashCode() {
        return request.getId().hashCode();
    }
}
