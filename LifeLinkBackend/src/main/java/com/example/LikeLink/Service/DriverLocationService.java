package com.example.LikeLink.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;

import com.example.LikeLink.Model.AmbulanceDriver;
import com.example.LikeLink.Model.Location;
import com.example.LikeLink.Repository.AmbulanceDriverRepository;
import com.example.LikeLink.Exception.DriverNotFoundException;
import com.example.LikeLink.Exception.InvalidLocationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;

@Service
@Slf4j
@RequiredArgsConstructor
public class DriverLocationService {

    private final AmbulanceDriverRepository driverRepository;
    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double MAX_SEARCH_RADIUS_KM = 10.0;
    private static final double SUSPICIOUS_DISTANCE_KM = 100.0;

    @Transactional
    public Location updateDriverLocation(String driverId, double latitude, double longitude) {
        try {
            validateCoordinates(latitude, longitude);

            AmbulanceDriver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with id: " + driverId));

            Location newLocation = new Location(latitude, longitude);
            validateLocationUpdate(driver, newLocation);

            driver.setCurrentLocation(newLocation);
            driver.setUpdatedAt(LocalDateTime.now());
            
            AmbulanceDriver updatedDriver = driverRepository.save(driver);
            log.info("Updated location for driver {}: [{}, {}]", driverId, longitude, latitude);
            
            return updatedDriver.getCurrentLocation();

        } catch (DriverNotFoundException | InvalidLocationException e) {
            log.warn(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error updating driver location: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update driver location", e);
        }
    }

    @Cacheable(value = "nearbyDrivers", key = "#latitude + '-' + #longitude")
    public List<AmbulanceDriver> findNearbyDrivers(double latitude, double longitude) {
        try {
//            validateCoordinates(latitude, longitude);

            List<AmbulanceDriver> allDrivers = driverRepository.findAll();
            
            return allDrivers;

        } catch (InvalidLocationException e) {
            log.warn(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error finding nearby drivers: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to find nearby drivers", e);
        }
    }


    private void validateCoordinates(double latitude, double longitude) {
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            throw new InvalidLocationException(
                String.format("Invalid coordinates: [%f, %f]", latitude, longitude));
        }
    }

    private void validateLocationUpdate(AmbulanceDriver driver, Location newLocation) {
        if (driver.getCurrentLocation() != null) {
            double distance = calculateDistance(
                driver.getCurrentLocation().getLatitude(),
                driver.getCurrentLocation().getLongitude(),
                newLocation.getLatitude(),
                newLocation.getLongitude()
            );

            if (distance > SUSPICIOUS_DISTANCE_KM) {
                log.warn("Suspicious location update for driver {}: Distance {} km", 
                    driver.getId(), distance);
                throw new InvalidLocationException(
                    "Location update rejected due to suspicious distance");
            }
        }
    }

    public int calculateEstimatedTime(Location driverLocation, Location userLocation) {
        try {
            double distance = calculateDistance(
                driverLocation.getLatitude(),
                driverLocation.getLongitude(),
                userLocation.getLatitude(),
                userLocation.getLongitude()
            );
            
            // Assume average speed of 40 km/h in city traffic
            double averageSpeedKmH = 40.0;
            return (int) Math.ceil((distance / averageSpeedKmH) * 60);
            
        } catch (Exception e) {
            log.error("Error calculating estimated time: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to calculate estimated time", e);
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
                   
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS_KM * c;
    }

    @Scheduled(fixedRate = 300000) 
    @CacheEvict(value = "nearbyDrivers", allEntries = true)
    public void clearNearbyDriversCache() {
        log.debug("Clearing nearby drivers cache");
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    private static class DriverDistance {
        private AmbulanceDriver driver;
        private double distance;
    }
}