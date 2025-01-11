package com.example.LikeLink.Service;

import com.example.LikeLink.Repository.AmbulanceDriverRepository;
import com.example.LikeLink.Repository.UserRepository;
import com.example.LikeLink.Model.AmbulanceDriver;
import com.example.LikeLink.Model.AmbulanceDriverRegistrationDto;
import com.example.LikeLink.Model.Location;
import com.example.LikeLink.Model.User;
import com.example.LikeLink.Enum.VerificationStatus;
import com.example.LikeLink.Exception.DriverAlreadyExistsException;
import com.example.LikeLink.Exception.DriverNotFoundException;
import com.example.LikeLink.Exception.UserNotFoundException;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AmbulanceDriverService {

    private final AmbulanceDriverRepository driverRepository;
    private final UserRepository userRepository;

    @Transactional
    public AmbulanceDriver registerDriver(String email, AmbulanceDriverRegistrationDto dto) {
        log.info("Processing registration for driver with email: {}", email);

        // Verify user exists
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        if (driverRepository.existsByEmail(email)) {
            throw new DriverAlreadyExistsException("Driver profile already exists for email: " + email);
        }

        // Validate unique fields
        validateUniqueFields(dto);

        AmbulanceDriver driver = new AmbulanceDriver();
        
        // Set required fields from DTO
        driver.setEmail(user.getEmail());
        driver.setFullName(dto.getFullName());
        driver.setDateOfBirth(dto.getDateOfBirth());
        driver.setPhoneNumber(dto.getPhoneNumber());
        driver.setCurrentAddress(dto.getCurrentAddress());
        driver.setDriversLicenseNumber(dto.getDriversLicenseNumber());
        driver.setLicenseType(dto.getLicenseType());
        driver.setExperienceWithEmergencyVehicle(dto.isExperienceWithEmergencyVehicle());
        driver.setYearsOfEmergencyExperience(dto.getYearsOfEmergencyExperience());
        driver.setVehicleRegistrationNumber(dto.getVehicleRegistrationNumber());
        driver.setHasAirConditioning(dto.isHasAirConditioning());
        driver.setHasOxygenCylinderHolder(dto.isHasOxygenCylinderHolder());
        driver.setHasStretcher(dto.isHasStretcher());
        driver.setInsurancePolicyNumber(dto.getInsurancePolicyNumber());
        driver.setInsuranceExpiryDate(dto.getInsuranceExpiryDate());
        driver.setCurrentLocation(new Location(0.0, 0.0)); 

        log.info("Saving new driver profile for: {}", dto.getFullName());
        return driverRepository.save(driver);
    }

    private void validateUniqueFields(AmbulanceDriverRegistrationDto dto) {
        if (driverRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new DriverAlreadyExistsException("Phone number already registered");
        }
        if (driverRepository.existsByDriversLicenseNumber(dto.getDriversLicenseNumber())) {
            throw new DriverAlreadyExistsException("Driver's license number already registered");
        }
    }

    @Transactional
    public AmbulanceDriver updateDriver(String email, AmbulanceDriverRegistrationDto dto) {
        log.info("Updating driver profile for email: {}", email);

        // Verify user exists
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        // Get existing driver
        AmbulanceDriver existingDriver = driverRepository.findByEmail(email)
            .orElseThrow(() -> new DriverNotFoundException("Driver not found for email: " + email));

        // Validate unique fields excluding current driver's data
        validateUniqueFieldsForUpdate(dto, existingDriver);

        // Update fields
        existingDriver.setFullName(dto.getFullName());
        existingDriver.setDateOfBirth(dto.getDateOfBirth());
        existingDriver.setPhoneNumber(dto.getPhoneNumber());
        existingDriver.setCurrentAddress(dto.getCurrentAddress());
        existingDriver.setDriversLicenseNumber(dto.getDriversLicenseNumber());
        existingDriver.setLicenseType(dto.getLicenseType());
        existingDriver.setExperienceWithEmergencyVehicle(dto.isExperienceWithEmergencyVehicle());
        existingDriver.setYearsOfEmergencyExperience(dto.getYearsOfEmergencyExperience());
        existingDriver.setVehicleRegistrationNumber(dto.getVehicleRegistrationNumber());
        existingDriver.setHasAirConditioning(dto.isHasAirConditioning());
        existingDriver.setHasOxygenCylinderHolder(dto.isHasOxygenCylinderHolder());
        existingDriver.setHasStretcher(dto.isHasStretcher());
        existingDriver.setInsurancePolicyNumber(dto.getInsurancePolicyNumber());
        existingDriver.setInsuranceExpiryDate(dto.getInsuranceExpiryDate());

        log.info("Updating driver profile for: {}", dto.getFullName());
        return driverRepository.save(existingDriver);
    }

    private void validateUniqueFieldsForUpdate(AmbulanceDriverRegistrationDto dto, AmbulanceDriver existingDriver) {
        if (!dto.getPhoneNumber().equals(existingDriver.getPhoneNumber()) && 
            driverRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new DriverAlreadyExistsException("Phone number already registered");
        }
        if (!dto.getDriversLicenseNumber().equals(existingDriver.getDriversLicenseNumber()) && 
            driverRepository.existsByDriversLicenseNumber(dto.getDriversLicenseNumber())) {
            throw new DriverAlreadyExistsException("Driver's license number already registered");
        }
    }

    public AmbulanceDriver getDriverByEmail(String email) {
        return driverRepository.findByEmail(email)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found for email: " + email));
    }

    @Transactional
    public void updateDriverStatus(String email, boolean available) {
        AmbulanceDriver driver = getDriverByEmail(email);
        driverRepository.save(driver);
        log.info("Updated status for driver: {} to: {}", email, available);
    }

    @Transactional
    public void updateDriverLocation(String email, Location location) {
        AmbulanceDriver driver = getDriverByEmail(email);
        driver.setCurrentLocation(location);
        driverRepository.save(driver);
        log.info("Updated location for driver: {} to: {}", email, location);
    }



    @Transactional
    public AmbulanceDriver verifyDriver(String licenseNumber, VerificationStatus status, String comment) {
        AmbulanceDriver driver = driverRepository.findByDriversLicenseNumber(licenseNumber)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with license: " + licenseNumber));

        
        AmbulanceDriver updatedDriver = driverRepository.save(driver);
        log.info("Updated verification status for driver: {} to: {}", licenseNumber, status);
        return updatedDriver;
    }
    
    
}