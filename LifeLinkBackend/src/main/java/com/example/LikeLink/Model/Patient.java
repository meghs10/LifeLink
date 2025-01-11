package com.example.LikeLink.Model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.LikeLink.Enum.BloodType;
import com.example.LikeLink.Enum.Gender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "patients")
public class Patient {
    @Id
    private String id;
    private String fullName;
    private int age;
    private String email;
    private Gender gender;
    private List<EmergencyContact> emergencyContacts;
    private List<String> medicalHistory;
    private List<PastSurgery> pastSurgeries;
    private List<Medication> currentMedications;
    private List<String> allergies;
    private BloodType bloodType;
    private double weight; 
    private double height; 
    private List<String> dietaryRestrictions;
    private boolean organDonor;
    private List<String> culturalConsiderations;
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime lastUpdatedAt;
}



