package com.example.LikeLink.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "blood_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BloodRequest {
    @Id
    private String id;
    private String hospitalId;
    private String hospitalName;
    private String phoneNumber;
    private String address;
    private String bloodType;
    private String status; 
}
