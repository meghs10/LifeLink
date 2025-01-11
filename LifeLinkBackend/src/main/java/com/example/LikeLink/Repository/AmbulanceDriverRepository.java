package com.example.LikeLink.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.example.LikeLink.Enum.VerificationStatus;
import com.example.LikeLink.Model.AmbulanceDriver;

import java.util.List;
import java.util.Optional;

public interface AmbulanceDriverRepository extends MongoRepository<AmbulanceDriver, String> {
    
    Optional<AmbulanceDriver> findByEmail(String email);
    
    Optional<AmbulanceDriver> findByPhoneNumber(String phoneNumber);
    
    Optional<AmbulanceDriver> findByDriversLicenseNumber(String licenseNumber);
    
    Optional<AmbulanceDriver> findByVehicleRegistrationNumber(String regNumber);
    
    Optional<AmbulanceDriver> findByInsurancePolicyNumber(String policyNumber); 
    
    
    @Query("{ 'currentLocation': { $exists: true, $ne: null }, 'currentLocation.coordinates': { $exists: true, $ne: null } }")
    List<AmbulanceDriver> findAll();

    
    boolean existsByEmail(String email);
    
    boolean existsByPhoneNumber(String phoneNumber);
    
    boolean existsByDriversLicenseNumber(String licenseNumber);
    
}
