package com.example.LikeLink.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.LikeLink.Exception.ResourceNotFoundException;
import com.example.LikeLink.Model.Booking;
import com.example.LikeLink.Model.Location;
import com.example.LikeLink.Service.BookingService;
import com.example.LikeLink.Service.DriverLocationService;
import com.example.LikeLink.dto.request.BookingRequest;
import com.example.LikeLink.dto.response.ApiResponse;
import com.example.LikeLink.dto.response.BookingResponse;
import com.example.LikeLink.dto.response.DriverLocation;
import com.example.LikeLink.dto.response.HospitalResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; 
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    private final DriverLocationService driverLocationService;


    @PostMapping("/request")
    public ResponseEntity<?> requestAmbulance(@Valid @RequestBody BookingRequest request) {
        try {
            BookingResponse response = bookingService.processBooking(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing booking request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Failed to process booking request", null));
        }
    }

    @GetMapping("/driver/assigned")
    public ResponseEntity<?> getAssignedBookings(@RequestParam String driverId) {
        try {
            List<Booking> bookings = bookingService.getAssignedBookings(driverId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Retrieved assigned bookings", bookings));
        } catch (Exception e) {
            log.error("Error retrieving assigned bookings", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Failed to retrieve assigned bookings", null));
        }
    }
    
    @GetMapping("/nearest-hospital")
    public ResponseEntity<HospitalResponse> findNearestHospital(
            @RequestParam double latitude,
            @RequestParam double longitude,
            Authentication authentication) {
        log.info("Finding nearest hospital for location: {}, {}", latitude, longitude);
        
        HospitalResponse nearestHospital = bookingService.findNearestHospital(new Location(latitude, longitude));
        
        return ResponseEntity.ok(nearestHospital);
    }
    
    @GetMapping("/drivers/{driverId}/location")
    public ResponseEntity<?> getDriverLocation(
            @PathVariable String driverId,
            Authentication authentication) {
        try {
            log.info("Fetching location for driver: {}", driverId);
            
            Location location = bookingService.getDriverLocation(driverId); 
            
            DriverLocation driverLocation = new DriverLocation(location.getLatitude(), location.getLongitude());
            
            if (driverLocation == null) {
                throw new ResourceNotFoundException("Driver location not found");
            }

            return ResponseEntity.ok(driverLocation);

        } catch (ResourceNotFoundException e) {
            log.warn("Driver location not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, e.getMessage(), null));

        } catch (Exception e) {
            log.error("Error fetching driver location", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Failed to fetch driver location", null));
        }
    }
    @GetMapping("/{driverId}")
    public ResponseEntity<?> getDriverBookings(Authentication authentication, @PathVariable String driverId) {
        try {
            List<Booking> bookings = bookingService.getDriverBookings(driverId);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            log.error("Error fetching driver bookings", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    // Get specific booking details
    @GetMapping("/driver/{id}")
    public ResponseEntity<?> getBookingDetails(
            @PathVariable String id,
            Authentication authentication) {
        try {
            String driverId = authentication.getName();
            Booking booking = bookingService.getBookingDetails(id, driverId);
            return ResponseEntity.ok(booking);
        } catch (ResourceNotFoundException e) {
            log.warn("Booking not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(null);
        } catch (Exception e) {
            log.error("Error fetching booking details", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    // Complete a booking
    @PostMapping("/{id}/complete")
    public ResponseEntity<?> completeBooking(
            @PathVariable String id,
            Authentication authentication) {
        try {
            String driverId = authentication.getName();
            Booking completedBooking = bookingService.completeBooking(id, driverId);
            return ResponseEntity.ok(completedBooking);
        } catch (ResourceNotFoundException e) {
            log.warn("Booking not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(null);
        } catch (Exception e) {
            log.error("Error completing booking", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }
    
    @PostMapping("/{bookingId}/status/complete")
    public ResponseEntity<?> updateBookingStatusToCompleted(@PathVariable String bookingId) {
        try {
            Booking updatedBooking = bookingService.updateBookingStatusToCompleted(bookingId);
            return ResponseEntity.ok( 
                updatedBooking
            );
        } catch (ResourceNotFoundException e) {
            log.warn("Booking not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                    null
                );
        } catch (Exception e) {
            log.error("Error updating booking status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                    null
                );
        }
    }
    


}
