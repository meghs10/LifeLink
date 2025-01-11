package com.example.LikeLink.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.LikeLink.Service.PatientService;
import com.example.LikeLink.dto.request.EmergencyContactDto;
import com.example.LikeLink.dto.request.MedicationDto;
import com.example.LikeLink.dto.request.PatientRequest;
import com.example.LikeLink.dto.response.PatientResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
@RestController
@RequestMapping("/api/patient")
@PreAuthorize("hasRole('PATIENT')")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PatientController {

    private final PatientService patientService;

    @PostMapping("/profile")
    public ResponseEntity<PatientResponse> createProfile(
            @Valid @RequestBody PatientRequest patientRequest,
            Authentication authentication) {
        log.info("Creating profile for user: {}", authentication.getName());
        PatientResponse savedPatient = patientService.createProfile(patientRequest, authentication.getName());
        return ResponseEntity.ok(savedPatient);
    }

    @GetMapping("/profile")
    public ResponseEntity<PatientResponse> getProfile(Authentication authentication) {
        log.info("Fetching profile for user: {}", authentication.getName());
        PatientResponse patient = patientService.getProfile(authentication.getName());
        return ResponseEntity.ok(patient);
    }

    @PutMapping("/profile")
    public ResponseEntity<PatientResponse> updateProfile(
            @Valid @RequestBody PatientRequest patientRequest,
            Authentication authentication) {
        log.info("Updating profile for user: {}", authentication.getName());
        PatientResponse updatedPatient = patientService.updateProfile(patientRequest, authentication.getName());
        return ResponseEntity.ok(updatedPatient);
    }

    @PatchMapping("/profile/allergies")
    public ResponseEntity<PatientResponse> updateAllergies(
            @Valid @RequestBody List<String> allergies,
            Authentication authentication) {
        log.info("Updating allergies for user: {}", authentication.getName());
        return ResponseEntity.ok(patientService.updateAllergies(allergies, authentication.getName()));
    }

    @PatchMapping("/profile/medical-history")
    public ResponseEntity<PatientResponse> updateMedicalHistory(
            @Valid @RequestBody List<String> medicalHistory,
            Authentication authentication) {
        log.info("Updating medical history for user: {}", authentication.getName());
        return ResponseEntity.ok(patientService.updateMedicalHistory(medicalHistory, authentication.getName()));
    }

    @PatchMapping("/profile/dietary-restrictions")
    public ResponseEntity<PatientResponse> updateDietaryRestrictions(
            @Valid @RequestBody List<String> restrictions,
            Authentication authentication) {
        log.info("Updating dietary restrictions for user: {}", authentication.getName());
        return ResponseEntity.ok(patientService.updateDietaryRestrictions(restrictions, authentication.getName()));
    }

    @PatchMapping("/profile/cultural-considerations")
    public ResponseEntity<PatientResponse> updateCulturalConsiderations(
            @Valid @RequestBody List<String> considerations,
            Authentication authentication) {
        log.info("Updating cultural considerations for user: {}", authentication.getName());
        return ResponseEntity.ok(patientService.updateCulturalConsiderations(considerations, authentication.getName()));
    }

    @PatchMapping("/profile/organ-donor")
    public ResponseEntity<PatientResponse> updateOrganDonorStatus(
            @Valid @RequestBody boolean isOrganDonor,
            Authentication authentication) {
        log.info("Updating organ donor status for user: {}", authentication.getName());
        return ResponseEntity.ok(patientService.updateOrganDonorStatus(isOrganDonor, authentication.getName()));
    }
}