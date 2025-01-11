package com.example.LikeLink.dto.request;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.LikeLink.Enum.BloodType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "blood_requests")
@Data
@NoArgsConstructor
public class BloodRequest {
    @Id
    private String id;
    
    private String hospitalId;
    private String patientId;
    private BloodType bloodType;
    private String status;
   

    public BloodRequest(String hospitalId, String patientId, BloodType bloodType, String status) {
        this.hospitalId = hospitalId;
        this.patientId = patientId;
        this.bloodType = bloodType;
        this.status = status;
    }

}
