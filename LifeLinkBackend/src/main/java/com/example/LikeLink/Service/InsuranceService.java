package com.example.LikeLink.Service;

import org.springframework.stereotype.Service;

import com.example.LikeLink.Enum.InsuranceType;
import com.example.LikeLink.Exception.ResourceNotFoundException;
import com.example.LikeLink.Model.Insurance;
import com.example.LikeLink.Model.User;
import com.example.LikeLink.Repository.InsuranceRepository;
import com.example.LikeLink.Repository.UserRepository;
import com.example.LikeLink.dto.request.InsuranceRequest;
import com.example.LikeLink.dto.response.InsuranceResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InsuranceService {

    private final InsuranceRepository insuranceRepository;
    private final UserRepository userRepository;

    public InsuranceResponse addInsurance(InsuranceRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Insurance insurance = Insurance.builder()
                .insuranceProviderName(request.getInsuranceProviderName())
                .policyNumber(request.getPolicyNumber())
                .groupNumber(request.getGroupNumber())
                .insuranceType(request.getInsuranceType())
                .policyHolderName(request.getPolicyHolderName())
                .relationshipToPolicyHolder(request.getRelationshipToPolicyHolder())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .planType(request.getPlanType())
                .coversEmergencyService(request.isCoversEmergencyService())
                .coversAmbulanceService(request.isCoversAmbulanceService())
                .userId(user.getId())
                .build();

        insurance.initializeTimestamps();
        
        Insurance savedInsurance = insuranceRepository.save(insurance);
        return mapToResponse(savedInsurance);
    }

    public List<InsuranceResponse> getAllInsurance(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return insuranceRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public InsuranceResponse getInsurance(String insuranceId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Insurance insurance = insuranceRepository.findByIdAndUserId(insuranceId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Insurance not found"));

        return mapToResponse(insurance);
    }

    public InsuranceResponse updateInsurance(String insuranceId, InsuranceRequest request, String userEmail) {
        Insurance insurance = getInsuranceForUser(insuranceId, userEmail);

        insurance.setInsuranceProviderName(request.getInsuranceProviderName());
        insurance.setPolicyNumber(request.getPolicyNumber());
        insurance.setGroupNumber(request.getGroupNumber());
        insurance.setInsuranceType(request.getInsuranceType());
        insurance.setPolicyHolderName(request.getPolicyHolderName());
        insurance.setRelationshipToPolicyHolder(request.getRelationshipToPolicyHolder());
        insurance.setStartDate(request.getStartDate());
        insurance.setEndDate(request.getEndDate());
        insurance.setPlanType(request.getPlanType());
        insurance.setCoversEmergencyService(request.isCoversEmergencyService());
        insurance.setCoversAmbulanceService(request.isCoversAmbulanceService());

        insurance.initializeTimestamps();
        
        Insurance updatedInsurance = insuranceRepository.save(insurance);
        return mapToResponse(updatedInsurance);
    }

    public void deleteInsurance(String insuranceId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Insurance insurance = insuranceRepository.findByIdAndUserId(insuranceId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Insurance not found"));

        insuranceRepository.delete(insurance);
    }

    public List<InsuranceResponse> getActiveInsurance(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LocalDate now = LocalDate.now();
        return insuranceRepository.findByUserId(user.getId())
                .stream()
                .filter(insurance -> isActive(insurance, now))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<InsuranceResponse> getActiveInsuranceByUserId(String userId) {

        LocalDate now = LocalDate.now();
        return insuranceRepository.findByUserId(userId)
                .stream()
                .filter(insurance -> isActive(insurance, now))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    

    public List<InsuranceResponse> getInsuranceByType(InsuranceType type, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return insuranceRepository.findByUserIdAndInsuranceType(user.getId(), type)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public InsuranceResponse updateEmergencyServiceCoverage(String insuranceId, boolean covers, String userEmail) {
        Insurance insurance = getInsuranceForUser(insuranceId, userEmail);
        insurance.setCoversEmergencyService(covers);
        Insurance updatedInsurance = insuranceRepository.save(insurance);
        return mapToResponse(updatedInsurance);
    }

    public InsuranceResponse updateAmbulanceServiceCoverage(String insuranceId, boolean covers, String userEmail) {
        Insurance insurance = getInsuranceForUser(insuranceId, userEmail);
        insurance.setCoversAmbulanceService(covers);
        Insurance updatedInsurance = insuranceRepository.save(insurance);
        return mapToResponse(updatedInsurance);
    }

    private Insurance getInsuranceForUser(String insuranceId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return insuranceRepository.findByIdAndUserId(insuranceId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Insurance not found"));
    }

    private boolean isActive(Insurance insurance, LocalDate currentDate) {
        return insurance.getStartDate().isBefore(currentDate) &&
                (insurance.getEndDate() == null || insurance.getEndDate().isAfter(currentDate));
    }

    private InsuranceResponse mapToResponse(Insurance insurance) {
        return InsuranceResponse.builder()
                .id(insurance.getId())
                .insuranceProviderName(insurance.getInsuranceProviderName())
                .policyNumber(insurance.getPolicyNumber())
                .groupNumber(insurance.getGroupNumber())
                .insuranceType(insurance.getInsuranceType())
                .policyHolderName(insurance.getPolicyHolderName())
                .relationshipToPolicyHolder(insurance.getRelationshipToPolicyHolder())
                .startDate(insurance.getStartDate())
                .endDate(insurance.getEndDate())
                .planType(insurance.getPlanType())
                .coversEmergencyService(insurance.isCoversEmergencyService())
                .coversAmbulanceService(insurance.isCoversAmbulanceService())
                .createdAt(insurance.getCreatedAt() != null ? insurance.getCreatedAt().toString() : null)
                .lastUpdatedAt(insurance.getLastUpdatedAt() != null ? insurance.getLastUpdatedAt().toString() : null)
                .build();
    }
}
