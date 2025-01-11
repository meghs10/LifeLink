package com.example.LikeLink.Controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.LikeLink.Model.BloodRequest;
import com.example.LikeLink.Model.Hospital;
import com.example.LikeLink.Service.BloodRequestService;
import com.example.LikeLink.Service.HospitalService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@RestController
@RequestMapping("/api/blood-requests")
@RequiredArgsConstructor
@Slf4j
public class BloodRequestController {

    private final BloodRequestService bloodRequestService;
    private final HospitalService hospitalService;

    @PostMapping
    public ResponseEntity<?> createBloodRequest(
            @RequestBody BloodRequest requestDTO,
            Authentication authentication) {
        try {
            BloodRequest request = bloodRequestService.createBloodRequest(requestDTO);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            log.error("Error creating blood request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    @GetMapping
    public ResponseEntity<?> getPendingRequests(Authentication authentication) {
        try {
            List<BloodRequest> requests = bloodRequestService.getPendingRequests();
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            log.error("Error fetching blood requests", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    @PutMapping("/{requestId}/status")
    public ResponseEntity<?> updateRequestStatus(
            @PathVariable String requestId,
            @RequestParam String status,
            Authentication authentication) {
        try {
            BloodRequest request = bloodRequestService.updateRequestStatus(requestId, status);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            log.error("Error updating blood request status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<?> getHospitalRequests(
            @PathVariable String hospitalId,
            Authentication authentication) {
        try {
            List<BloodRequest> requests = bloodRequestService.getHospitalRequests(hospitalId);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            log.error("Error fetching hospital blood requests", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }
    @GetMapping("/hospitalUI/{hospitalId}")
    public ResponseEntity<Hospital> getHospitalDetails(@PathVariable String hospitalId) {
        Hospital hospital = hospitalService.getHospitalById(hospitalId);
        
        if (hospital == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(hospital);
    } 
}
