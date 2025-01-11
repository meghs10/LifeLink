package com.hospital.lifelinkhospitals.model;

public class EmergencyContactResponse {
    private String contactName;
    private String phoneNumber;

    public String getContactName() { return contactName; }
    public void setContactName(String name) { this.contactName = name; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String number) { this.phoneNumber = number; }
}
