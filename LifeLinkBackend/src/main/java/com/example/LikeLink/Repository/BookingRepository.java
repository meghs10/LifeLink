package com.example.LikeLink.Repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.LikeLink.Enum.BookingStatus;
import com.example.LikeLink.Model.Booking;


@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {
    
    // Find bookings assigned to a specific driver with given status
    List<Booking> findByDriverIdAndStatus(String driverId, BookingStatus status);
    
    // Find active bookings for a user (not completed or cancelled)
    @Query("{'userId': ?0, 'status': { $nin: ['COMPLETED', 'CANCELLED'] }}")
    List<Booking> findActiveBookingsByUserId(String userId);
    
    // Find latest booking for a user
    Optional<Booking> findFirstByUserIdOrderByCreatedAtDesc(String userId); 
    
    List<Booking> findByDriverId(String driverId);
    
    // Find nearby bookings
    @Query("{'pickupLocation': {" +
           "  $nearSphere: {" +
           "    $geometry: {" +
           "      type: 'Point'," +
           "      coordinates: [?0, ?1]" +
           "    }," +
           "    $maxDistance: ?2" +
           "  }" +
           "}}")
    List<Booking> findNearbyBookings(Double longitude, Double latitude, Double radiusInMeters);
}
