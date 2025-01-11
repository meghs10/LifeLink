package com.example.LikeLink.Controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.LikeLink.Exception.DriverAlreadyExistsException;
import com.example.LikeLink.Exception.DriverNotFoundException;
import com.example.LikeLink.Exception.UserNotFoundException;
import com.example.LikeLink.Model.AmbulanceDriver;
import com.example.LikeLink.Model.AmbulanceDriverRegistrationDto;
import com.example.LikeLink.Model.Location;
import com.example.LikeLink.Model.LocationUpdateDto;
import com.example.LikeLink.Service.AmbulanceDriverService;
import com.example.LikeLink.dto.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/ambulance")
@RequiredArgsConstructor
@Slf4j
public class AmbulanceController {

    private final AmbulanceDriverService driverService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('AMBULANCE_DRIVER')")
    public ResponseEntity<?> registerDriver(
            @Valid @RequestBody AmbulanceDriverRegistrationDto registrationDto,
            Authentication authentication) {
        log.info("Received registration request for driver: {}", registrationDto.getFullName());
        try {
            String email = authentication.getName();
            AmbulanceDriver driver = driverService.registerDriver(email, registrationDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Driver registered successfully", driver));
        } catch (DriverAlreadyExistsException e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('AMBULANCE_DRIVER')")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        try {
            String email = authentication.getName();
            log.info("Fetching profile for driver with email: {}", email);
            AmbulanceDriver driver = driverService.getDriverByEmail(email);
            return ResponseEntity.ok(new ApiResponse<>(true, "Profile fetched successfully", driver));
        } catch (DriverNotFoundException e) {
            log.error("Profile fetch failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('AMBULANCE_DRIVER')")
    public ResponseEntity<?> updateDriver(
            @Valid @RequestBody AmbulanceDriverRegistrationDto updateDto,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            log.info("Updating driver profile for email: {}", email);
            
            AmbulanceDriver updatedDriver = driverService.updateDriver(email, updateDto);
            return ResponseEntity.ok(new ApiResponse<>(true, "Driver profile updated successfully", updatedDriver));
        } catch (UserNotFoundException | DriverNotFoundException e) {
            log.error("Update failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (DriverAlreadyExistsException e) {
            log.error("Update failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PutMapping("/location")
    @PreAuthorize("hasRole('AMBULANCE_DRIVER')")
    public ResponseEntity<?> updateLocation(
            @Valid @RequestBody LocationUpdateDto locationDto,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            log.info("Updating location for driver: {} to: {}", email, locationDto);
            Location location = new Location(locationDto.getLatitude(), locationDto.getLongitude());
            driverService.updateDriverLocation(email, location);
            return ResponseEntity.ok(location);
        } catch (DriverNotFoundException e) {
            log.error("Location update failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
    }
    
    @GetMapping("/driver/{email}")
    @PreAuthorize("hasRole('AMBULANCE_DRIVER')") 
    public ResponseEntity<?> getDriverIdByUserId(
    		Authentication authenticaion,
    		@PathVariable String email){ 
    	
    	AmbulanceDriver driver = driverService.getDriverByEmail(email);  
    	if(driver == null) return new ResponseEntity(HttpStatus.NOT_FOUND);	
    	return ResponseEntity.ok(driver);
    }
}