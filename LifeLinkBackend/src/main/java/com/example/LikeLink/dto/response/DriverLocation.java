package com.example.LikeLink.dto.response;


public class DriverLocation {

    private double latitude;


    private double longitude;

    // Default constructor
    public DriverLocation() {
    }

    // Constructor with parameters
    public DriverLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    // Setters
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "DriverLocation{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
