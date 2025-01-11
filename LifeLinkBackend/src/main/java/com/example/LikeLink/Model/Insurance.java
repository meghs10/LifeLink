package com.example.LikeLink.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.LikeLink.Enum.InsuranceType;
import com.example.LikeLink.Enum.PlanType;
import com.example.LikeLink.Enum.RelationshipType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "insurances")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Insurance {
    @Id
    private String id;

    @NotBlank
    private String insuranceProviderName;

    @NotBlank
    private String policyNumber;

    private String groupNumber;

    @NotNull
    private InsuranceType insuranceType;

    @NotBlank
    private String policyHolderName;

    @NotNull
    private RelationshipType relationshipToPolicyHolder;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull
    private PlanType planType;

    private boolean coversEmergencyService;

    private boolean coversAmbulanceService;

    @NotNull
    private String userId;  

    private LocalDateTime createdAt;

    private LocalDateTime lastUpdatedAt;
    
    public void initializeTimestamps() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        lastUpdatedAt = LocalDateTime.now();
    }
}