package com.example.LikeLink.dto.request;

import java.time.LocalDate;

import com.example.LikeLink.Enum.InsuranceType;
import com.example.LikeLink.Enum.PlanType;
import com.example.LikeLink.Enum.RelationshipType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InsuranceRequest {
    @NotBlank(message = "Insurance provider name is required")
    private String insuranceProviderName;

    @NotBlank(message = "Policy number is required")
    private String policyNumber;

    private String groupNumber; 

    @NotNull(message = "Insurance type is required")
    private InsuranceType insuranceType;

    @NotBlank(message = "Policy holder name is required")
    private String policyHolderName;

    @NotNull(message = "Relationship to policy holder is required")
    private RelationshipType relationshipToPolicyHolder;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate; 

    @NotNull(message = "Plan type is required")
    private PlanType planType;

    private boolean coversEmergencyService;

    private boolean coversAmbulanceService;
}