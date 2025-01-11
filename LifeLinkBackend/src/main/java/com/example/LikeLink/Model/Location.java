package com.example.LikeLink.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Location {
    private String type = "Point";
    private double[] coordinates; // [longitude, latitude]

    public Location(double latitude, double longitude) {
        this.coordinates = new double[]{longitude, latitude};
    }
    public double getLatitude() {
        return coordinates != null && coordinates.length > 1 ? coordinates[1] : 0.0;
    }

    public double getLongitude() {
        return coordinates != null && coordinates.length > 0 ? coordinates[0] : 0.0;
    }

    // Validation method
    public boolean isValid() {
        return coordinates != null && coordinates.length == 2;
    }
}