package com.example.LikeLink.dto.response;

import lombok.Data;

@Data
public class DriverResponse {
    private String bookingId;
    private String driverId;
    private boolean accepted;
}
