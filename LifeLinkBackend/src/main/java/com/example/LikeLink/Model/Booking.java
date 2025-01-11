package com.example.LikeLink.Model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.LikeLink.Enum.BookingStatus;
import com.example.LikeLink.dto.request.LocationRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;



@Document(collection = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    
    @Id
    private String id;

    @NotNull
    @Indexed
    private String userId;

    @Indexed
    private String driverId;

    @NotNull
    private Location pickupLocation;

    private Location destinationLocation;

    @NotNull
    @Indexed
    private BookingStatus status;

    @Indexed
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Helper methods
    public boolean isActive() {
        return status != BookingStatus.COMPLETED && 
               status != BookingStatus.CANCELLED;
    }

    public void updateStatus(BookingStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    public void assignDriver(String driverId) {
        this.driverId = driverId;
        this.status = BookingStatus.ASSIGNED;
        this.updatedAt = LocalDateTime.now();
    }
}