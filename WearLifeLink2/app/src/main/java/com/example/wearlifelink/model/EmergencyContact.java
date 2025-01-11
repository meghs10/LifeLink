package com.example.wearlifelink.model;


public class EmergencyContact {
    private long id;
    private String name;
    private String phoneNumber;
    private String email;
    private boolean isPrimary;

    // Constructor
    public EmergencyContact(String name, String phoneNumber, String email, boolean isPrimary) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.isPrimary = isPrimary;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    // Setters
    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

}
