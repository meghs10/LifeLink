package com.example.LikeLink.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmergencyContactDto {
    @NotBlank(message = "Contact name is required")
    private String contactName;
    
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
}

