package com.example.LikeLink.dto.request;


import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

@Data
public class HospitalRegistrationRequest {
    
    @NotBlank(message = "Hospital name is required")
    private String hospitalName;

    @NotBlank(message = "Hospital type is required")
    private String hospitalType;

    @NotBlank(message = "License number is required")
    private String licenseNumber;

    @NotBlank(message = "Year established is required")
    private String yearEstablished;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid phone number format")
    private String phoneNumber;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "PIN code is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "Invalid PIN code format")
    private String pinCode;

    @NotNull(message = "Latitude is required")
    private double latitude;

    @NotNull(message = "Longitude is required")
    private double longitude;

    @Min(value = 0, message = "Total beds must be positive")
    private int totalBeds;

    @Min(value = 0, message = "ICU beds must be positive")
    private int icuBeds;

    @Min(value = 0, message = "Emergency beds must be positive")
    private int emergencyBeds;

    private boolean hasAmbulanceService;
    private boolean hasEmergencyService;
    private List<String> departments;
}
