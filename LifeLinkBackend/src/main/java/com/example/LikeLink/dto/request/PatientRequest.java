package com.example.LikeLink.dto.request;

import java.util.List;

import com.example.LikeLink.Enum.BloodType;
import com.example.LikeLink.Enum.Gender;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PatientRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age must be positive")
    private int age;

    @NotNull(message = "Gender is required")
    private Gender gender;

    private List<EmergencyContactDto> emergencyContacts;
    private List<String> medicalHistory;
    private List<PastSurgeryDto> pastSurgeries;
    private List<MedicationDto> currentMedications;
    private List<String> allergies;

    @NotNull(message = "Blood type is required")
    private BloodType bloodType;

    @NotNull(message = "Weight is required")
    @Min(value = 0, message = "Weight must be positive")
    private double weight;

    @NotNull(message = "Height is required")
    @Min(value = 0, message = "Height must be positive")
    private double height;

    private List<String> dietaryRestrictions;
    private boolean organDonor;
    private List<String> culturalConsiderations;
}





