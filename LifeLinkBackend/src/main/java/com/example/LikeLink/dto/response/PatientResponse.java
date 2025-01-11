package com.example.LikeLink.dto.response;


import java.util.List;

import com.example.LikeLink.Enum.BloodType;
import com.example.LikeLink.Enum.Gender;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatientResponse {
    private String id;
    private String fullName;
    private int age;
    private Gender gender;
    
    private List<EmergencyContactResponse> emergencyContacts;
    private List<String> medicalHistory;
    private List<PastSurgeryResponse> pastSurgeries;
    private List<MedicationResponse> currentMedications;
    private List<String> allergies;
    
    private BloodType bloodType;
    private double weight;
    private double height; 
    
    private List<String> dietaryRestrictions;
    private boolean organDonor;
    private List<String> culturalConsiderations;
    
    private String createdAt;
    private String lastUpdatedAt;
}



