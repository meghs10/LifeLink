package com.example.LikeLink.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationUpdateDto {
    private String driverId;
    private Double latitude;
    private Double longitude;
    private String bookingId; // optional, for tracking specific bookings
}