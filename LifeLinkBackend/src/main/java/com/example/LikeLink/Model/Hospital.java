package com.example.LikeLink.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@Document(collection = "hospitals")
public class Hospital {
    @Id
    private String id;
    private String userId;
    private String hospitalName;
    private String hospitalType;
    private String licenseNumber;
    private String yearEstablished;
    private String phoneNumber;
    private String address;
    private String city;
    private String state;
    private String pinCode;
    private double latitude;
    private double longitude;
    private int totalBeds;
    private int icuBeds;
    private int emergencyBeds;
    private boolean hasAmbulanceService;
    private boolean hasEmergencyService;
    private List<String> departments;
    private String createdAt;
    private String updatedAt;
}