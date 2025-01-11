package com.example.LikeLink.dto.request;

import com.example.LikeLink.Model.Location;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    @NotNull(message = "User ID is required")
    private String userId;

    @NotNull(message = "Pickup location is required")
    private Location pickupLocation;

    private Location destinationLocation;
}
