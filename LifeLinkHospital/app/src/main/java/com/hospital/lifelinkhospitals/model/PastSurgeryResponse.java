package com.hospital.lifelinkhospitals.model;

public class PastSurgeryResponse {
    private String surgeryType;
    private String approximateDate;

    public String getSurgeryType() { return surgeryType; }
    public void setSurgeryType(String type) { this.surgeryType = type; }

    public String getApproximateDate() { return approximateDate; }
    public void setApproximateDate(String date) { this.approximateDate = date; }
}
