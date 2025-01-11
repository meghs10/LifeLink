package com.example.LikeLink.Model;


import com.example.LikeLink.Enum.LicenseType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmbulanceDriverRegistrationDto {
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Date of birth is required")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Date should be in YYYY-MM-DD format")
    private String dateOfBirth;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number")
    private String phoneNumber;

    @NotBlank(message = "Current address is required")
    private String currentAddress;

    @NotBlank(message = "Driver's license number is required")
    private String driversLicenseNumber;

    @NotNull(message = "License type is required")
    private LicenseType licenseType;

    private boolean experienceWithEmergencyVehicle;

    private Integer yearsOfEmergencyExperience;

    @NotBlank(message = "Vehicle registration number is required")
    private String vehicleRegistrationNumber;

    private boolean hasAirConditioning;
    private boolean hasOxygenCylinderHolder;
    private boolean hasStretcher;

    @NotBlank(message = "Insurance policy number is required")
    private String insurancePolicyNumber;

    @NotBlank(message = "Insurance expiry date is required")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Date should be in YYYY-MM-DD format")
    private String insuranceExpiryDate;
}
