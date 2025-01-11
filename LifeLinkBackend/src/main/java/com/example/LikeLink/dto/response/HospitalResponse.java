package com.example.LikeLink.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HospitalResponse {
    private String hospitalId;
    private String hospitalName;
    private double latitude;
    private double longitude; 
}
