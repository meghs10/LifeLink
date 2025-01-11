package com.example.LikeLink.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.LikeLink.Exception.ResourceNotFoundException;
import com.example.LikeLink.Model.BloodRequest;
import com.example.LikeLink.Model.Hospital;
import com.example.LikeLink.Repository.BloodRequestRepository;
import com.example.LikeLink.Repository.HospitalRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; 
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BloodRequestService {

    private final BloodRequestRepository bloodRequestRepository;
    private final HospitalRepository hospitalRepository;

    public BloodRequest createBloodRequest(BloodRequest requestDTO) {
        log.info("Creating blood request for hospital: {}", requestDTO.getHospitalId());

        // Validate hospital
        Hospital hospital = hospitalRepository.findById(requestDTO.getHospitalId())
            .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));

        BloodRequest request = BloodRequest.builder()
            .hospitalId(hospital.getId())
            .hospitalName(hospital.getHospitalName())
            .phoneNumber(hospital.getPhoneNumber())
            .address(hospital.getAddress())
            .bloodType(requestDTO.getBloodType())
            .status("PENDING")
            .build();

        return bloodRequestRepository.save(request);
    }

    public List<BloodRequest> getPendingRequests() {
        log.info("Fetching all pending blood requests");
        return bloodRequestRepository.findByStatus("PENDING");
    }


    public List<BloodRequest> getHospitalRequests(String hospitalId) {
        log.info("Fetching blood requests for hospital: {}", hospitalId);
        return bloodRequestRepository.findByHospitalIdAndStatus(hospitalId, "PENDING");
    }
    
    @Transactional
    public BloodRequest updateRequestStatus(String requestId, String newStatus) {
        BloodRequest request = bloodRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Blood request not found"));

        // Validate status transition
        if (!isValidStatusTransition(request.getStatus(), newStatus)) {
            throw new IllegalStateException("Invalid status transition");
        }

        request.setStatus(newStatus);
        return bloodRequestRepository.save(request);
    }

    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        // Only allow PENDING requests to be ACCEPTED or REJECTED
        if ("PENDING".equals(currentStatus)) {
            return "ACCEPTED".equals(newStatus) || "REJECTED".equals(newStatus);
        }
        return false;
    }
}
