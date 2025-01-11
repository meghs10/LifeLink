package com.example.LikeLink.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.LikeLink.Enum.BookingStatus;
import com.example.LikeLink.Exception.BookingException;
import com.example.LikeLink.Exception.ResourceNotFoundException;
import com.example.LikeLink.Model.AmbulanceDriver;
import com.example.LikeLink.Model.BloodRequest;
import com.example.LikeLink.Model.Booking;
import com.example.LikeLink.Model.Hospital;
import com.example.LikeLink.Model.IncomingPatient;
import com.example.LikeLink.Model.Location;
import com.example.LikeLink.Model.Patient;
import com.example.LikeLink.Model.User;
import com.example.LikeLink.Repository.AmbulanceDriverRepository;
import com.example.LikeLink.Repository.BloodRequestRepository;
import com.example.LikeLink.Repository.BookingRepository;
import com.example.LikeLink.Repository.HospitalRepository;
import com.example.LikeLink.Repository.IncomingPatientRepository;
import com.example.LikeLink.Repository.PatientRepository;
import com.example.LikeLink.Repository.UserRepository;
import com.example.LikeLink.dto.request.BookingRequest;
import com.example.LikeLink.dto.response.BookingResponse;
import com.example.LikeLink.dto.response.HospitalResponse;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final DriverLocationService driverLocationService;
    private final HospitalRepository hospitalRepository;
    private final AmbulanceDriverRepository driverRepository;
    private final IncomingPatientRepository incomingPatientRepository; 
    private final UserRepository userRepository; 
    private final AuthService authService; 
    private final PatientRepository patientRepository;
    private final BloodRequestRepository bloodRequestRepository;
    private static final double SEARCH_RADIUS_KM = 5.0;
    
    
    private List<AmbulanceDriver> findNearbyDrivers(Location pickupLocation) {
        try {
            log.info("Searching for drivers near location: {}, {}", 
                pickupLocation.getLatitude(), 
                pickupLocation.getLongitude()
            );

            List<AmbulanceDriver> drivers = driverLocationService.findNearbyDrivers(
                pickupLocation.getLatitude(),
                pickupLocation.getLongitude()
            );
            
            // Filter out drivers that are not available
            List<AmbulanceDriver> availableDrivers = drivers.stream()  // Add this field to your AmbulanceDriver model
                .collect(Collectors.toList());

            log.info("Found {} total drivers, {} are available", 
                drivers.size(), availableDrivers.size());

            return availableDrivers;

        } catch (Exception e) {
            log.error("Error finding nearby drivers: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
    
    public HospitalResponse findNearestHospital(Location userLocation) {
        try {
            log.info("Finding nearest hospital for location: {}, {}", 
                userLocation.getLatitude(), userLocation.getLongitude());

            List<Hospital> allHospitals = hospitalRepository.findAll();
            if (allHospitals.isEmpty()) {
                throw new BookingException("No hospitals found in the system");
            }

            Hospital nearestHospital = allHospitals.stream()
                .min((h1, h2) -> {
                    double dist1 = calculateDistance(
                        h1.getLatitude(),
                        h1.getLongitude(),
                        userLocation.getLatitude(),
                        userLocation.getLongitude()
                    );
                    double dist2 = calculateDistance(
                        h2.getLatitude(),
                        h2.getLongitude(),
                        userLocation.getLatitude(),
                        userLocation.getLongitude()
                    );
                    return Double.compare(dist1, dist2);
                })
                .orElseThrow(() -> new BookingException("No hospital found"));


            // Convert to HospitalResponse
            HospitalResponse response = new HospitalResponse();
            response.setHospitalId(nearestHospital.getId());
            response.setHospitalName(nearestHospital.getHospitalName());
            response.setLatitude(nearestHospital.getLatitude());
            response.setLongitude(nearestHospital.getLongitude());

            log.info("Found nearest hospital: {} at distance: {}km", 
                nearestHospital.getHospitalName());

            return response;

        } catch (Exception e) {
            log.error("Error finding nearest hospital: {}", e.getMessage(), e);
            throw new BookingException("Unable to find nearest hospital", e);
        }
    }
   

    @Transactional
    public BookingResponse processBooking(BookingRequest request) {
        try {
            log.info("Processing booking request for user: {}", request.getUserId());
          

            // First find nearest hospital
            HospitalResponse nearestHospital = findNearestHospital(request.getPickupLocation());
            log.info("Found nearest hospital: {}", nearestHospital.getHospitalName());

            // Find nearby drivers BEFORE creating booking
            List<AmbulanceDriver> nearbyDrivers = findNearbyDrivers(request.getPickupLocation());
            log.info("Found {} nearby drivers", nearbyDrivers.size());

            if (nearbyDrivers.isEmpty()) {
                log.warn("No drivers found within {}km radius", SEARCH_RADIUS_KM);
                return new BookingResponse(
                    "No drivers available",
                    null,
                    null,
                    "CANCELLED",
                    null
                );
            }

            // Find nearest driver
            AmbulanceDriver nearestDriver = findNearestDriver(nearbyDrivers, request.getPickupLocation());
            log.info("Selected nearest driver: {}", nearestDriver.getId());

            // Create and save the booking
            Booking booking = new Booking(); 
            booking.setUserId(request.getUserId()); 
            booking.setDriverId(nearestDriver.getId()); 
            booking.setPickupLocation(request.getPickupLocation());
            booking.setDestinationLocation(request.getDestinationLocation()); 
            booking.setStatus(BookingStatus.ASSIGNED); 

            booking = bookingRepository.save(booking);
            log.info("Created booking with ID: {}", booking.getId());
            
            IncomingPatient incomingPatient = new IncomingPatient();
            incomingPatient.setHospitalId(nearestHospital.getHospitalId());  // Set ID as hospital ID
            incomingPatient.setUserId(request.getUserId());
            incomingPatientRepository.save(incomingPatient); 
            
            String userEmail = authService.getEmailByUserId(request.getUserId());  
            
            Optional<Patient> patient = patientRepository.findByEmail(userEmail); 
            if(!patient.isEmpty()) { 
            	Hospital hospital = hospitalRepository.findById(nearestHospital.getHospitalId())
                        .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));


                    BloodRequest brequest = BloodRequest.builder()
                        .hospitalId(hospital.getId())
                        .hospitalName(hospital.getHospitalName())
                        .phoneNumber(hospital.getPhoneNumber())
                        .address(hospital.getAddress())
                        .bloodType(patient.get().getBloodType().toString())
                        .status("PENDING")
                        .build();

                    bloodRequestRepository.save(brequest);
            }
            return new BookingResponse(
                "Driver assigned successfully",
                booking.getId(),
                nearestDriver.getId(),
                "ASSIGNED",
                nearestDriver.getId()
            );

        } catch (Exception e) {
            log.error("Error processing booking request: {}", e.getMessage(), e);
            return new BookingResponse(
                "Failed to process booking: " + e.getMessage(),
                null,
                null,
                "CANCELLED",
                null
            );
        }
    }

    private AmbulanceDriver findNearestDriver(List<AmbulanceDriver> drivers, Location pickupLocation) {
        return drivers.stream()
            .min((d1, d2) -> {
                double dist1 = calculateDistance(
                    d1.getCurrentLocation().getLatitude(),
                    d1.getCurrentLocation().getLongitude(),
                    pickupLocation.getLatitude(),
                    pickupLocation.getLongitude()
                );
                double dist2 = calculateDistance(
                    d2.getCurrentLocation().getLatitude(),
                    d2.getCurrentLocation().getLongitude(),
                    pickupLocation.getLatitude(),
                    pickupLocation.getLongitude()
                );
                return Double.compare(dist1, dist2);
            })
            .orElseThrow(() -> new RuntimeException("No drivers available"));
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Haversine formula
        double R = 6371; // Earth's radius in kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    private Integer calculateEstimatedTime(Location driverLocation, Location pickupLocation) {
        double distance = calculateDistance(
            driverLocation.getLatitude(),
            driverLocation.getLongitude(),
            pickupLocation.getLatitude(),
            pickupLocation.getLongitude()
        );
        // Assume average speed of 40 km/h
        return (int) Math.ceil(distance / 40.0 * 60);
    }

    public List<Booking> getAssignedBookings(String driverId) {
        return bookingRepository.findByDriverIdAndStatus(driverId, BookingStatus.ASSIGNED);
    }
    
    public Location getDriverLocation(String driverId) {
        log.info("Fetching location for driver: {}", driverId);
        
        AmbulanceDriver driver = driverRepository.findById(driverId)
            .orElseThrow(() -> new ResourceNotFoundException("Driver not found: " + driverId));

        if (driver.getCurrentLocation() == null) {
            throw new ResourceNotFoundException("Location not found for driver: " + driverId);
        }

        return driver.getCurrentLocation();
    }
    
    public List<Booking> getDriverBookings(String driverId) {
        log.info("Fetching active bookings for driver: {}", driverId);
        return bookingRepository.findByDriverIdAndStatus(driverId, BookingStatus.ASSIGNED);
    }

    // Get specific booking details
    public Booking getBookingDetails(String bookingId, String driverId) {
        log.info("Fetching booking details - ID: {}, Driver: {}", bookingId, driverId);
        return bookingRepository.findById(bookingId)
            .filter(booking -> booking.getDriverId().equals(driverId))
            .orElseThrow(() -> new ResourceNotFoundException(
                "Booking not found with id: " + bookingId));
    }

    // Complete a booking
    public Booking completeBooking(String bookingId, String driverId) {
        log.info("Completing booking - ID: {}, Driver: {}", bookingId, driverId);
        Booking booking = getBookingDetails(bookingId, driverId);
        
        booking.setStatus(BookingStatus.COMPLETED);
        
        return bookingRepository.save(booking);
    }
    
    public Booking updateBookingStatusToCompleted(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        
        // Update status to completed
        booking.updateStatus(BookingStatus.COMPLETED);
        
        Booking updatedBooking = bookingRepository.save(booking);
        
        log.info("Booking {} status updated to COMPLETED", bookingId);
        
        return updatedBooking;
    }
    
}