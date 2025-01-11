package com.example.LikeLink.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PastSurgeryDto {
    @NotBlank(message = "Surgery type is required")
    private String surgeryType;
    
    @NotBlank(message = "Date is required")
    private String approximateDate;
   
}
