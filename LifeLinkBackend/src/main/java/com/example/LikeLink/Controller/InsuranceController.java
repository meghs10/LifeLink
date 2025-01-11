package com.example.LikeLink.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.LikeLink.Enum.InsuranceType;
import com.example.LikeLink.Service.InsuranceService;
import com.example.LikeLink.dto.request.InsuranceRequest;
import com.example.LikeLink.dto.response.InsuranceResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List; 

@RestController
@RequestMapping("/api/insurance")
@PreAuthorize("hasRole('PATIENT')")
@RequiredArgsConstructor
@Validated
@Slf4j
public class InsuranceController {

    private final InsuranceService insuranceService;

    @PostMapping
    public ResponseEntity<InsuranceResponse> addInsurance(
            @Valid @RequestBody InsuranceRequest insuranceRequest,
            Authentication authentication) {
        log.info("Adding insurance for user: {}", authentication.getName());
        InsuranceResponse savedInsurance = insuranceService.addInsurance(insuranceRequest, authentication.getName());
        return ResponseEntity.ok(savedInsurance);
    }

    @GetMapping
    public ResponseEntity<List<InsuranceResponse>> getAllInsurance(Authentication authentication) {
        log.info("Fetching all insurance for user: {}", authentication.getName());
        List<InsuranceResponse> insurances = insuranceService.getAllInsurance(authentication.getName());
        return ResponseEntity.ok(insurances);
    }

    @GetMapping("/{insuranceId}")
    public ResponseEntity<InsuranceResponse> getInsurance(
            @PathVariable String insuranceId,
            Authentication authentication) {
        log.info("Fetching insurance {} for user: {}", insuranceId, authentication.getName());
        InsuranceResponse insurance = insuranceService.getInsurance(insuranceId, authentication.getName());
        return ResponseEntity.ok(insurance);
    }

    @PutMapping("/{insuranceId}")
    public ResponseEntity<InsuranceResponse> updateInsurance(
            @PathVariable String insuranceId,
            @Valid @RequestBody InsuranceRequest insuranceRequest,
            Authentication authentication) {
        log.info("Updating insurance {} for user: {}", insuranceId, authentication.getName());
        InsuranceResponse updatedInsurance = insuranceService.updateInsurance(
            insuranceId, insuranceRequest, authentication.getName());
        return ResponseEntity.ok(updatedInsurance);
    }

    @DeleteMapping("/{insuranceId}")
    public ResponseEntity<Void> deleteInsurance(
            @PathVariable String insuranceId,
            Authentication authentication) {
        log.info("Deleting insurance {} for user: {}", insuranceId, authentication.getName());
        insuranceService.deleteInsurance(insuranceId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active")
    public ResponseEntity<List<InsuranceResponse>> getActiveInsurance(Authentication authentication) {
        log.info("Fetching active insurance for user: {}", authentication.getName());
        List<InsuranceResponse> activeInsurances = insuranceService.getActiveInsurance(authentication.getName());
        return ResponseEntity.ok(activeInsurances);
    }

    @GetMapping("/type/{insuranceType}")
    public ResponseEntity<List<InsuranceResponse>> getInsuranceByType(
            @PathVariable InsuranceType insuranceType,
            Authentication authentication) {
        log.info("Fetching {} insurance for user: {}", insuranceType, authentication.getName());
        List<InsuranceResponse> insurances = insuranceService.getInsuranceByType(
            insuranceType, authentication.getName());
        return ResponseEntity.ok(insurances);
    }

    @PatchMapping("/{insuranceId}/emergency-service")
    public ResponseEntity<InsuranceResponse> updateEmergencyServiceCoverage(
            @PathVariable String insuranceId,
            @RequestParam boolean covers,
            Authentication authentication) {
        log.info("Updating emergency service coverage for insurance {} to {}", insuranceId, covers);
        InsuranceResponse updated = insuranceService.updateEmergencyServiceCoverage(
            insuranceId, covers, authentication.getName());
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{insuranceId}/ambulance-service")
    public ResponseEntity<InsuranceResponse> updateAmbulanceServiceCoverage(
            @PathVariable String insuranceId,
            @RequestParam boolean covers,
            Authentication authentication) {
        log.info("Updating ambulance service coverage for insurance {} to {}", insuranceId, covers);
        InsuranceResponse updated = insuranceService.updateAmbulanceServiceCoverage(
            insuranceId, covers, authentication.getName());
        return ResponseEntity.ok(updated);
    }
}
