package com.example.LikeLink.dto.response;

import java.time.LocalDate;

import com.example.LikeLink.Enum.InsuranceType;
import com.example.LikeLink.Enum.PlanType;
import com.example.LikeLink.Enum.RelationshipType;
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
public class InsuranceResponse {
    private String id;
    private String insuranceProviderName;
    private String policyNumber;
    private String groupNumber;
    private InsuranceType insuranceType;
    private String policyHolderName;
    private RelationshipType relationshipToPolicyHolder;
    private LocalDate startDate;
    private LocalDate endDate;
    private PlanType planType;
    private boolean coversEmergencyService;
    private boolean coversAmbulanceService;
    private String createdAt;
    private String lastUpdatedAt;
}
