package com.example.wearlifelink.util;

public class HealthDataValidator {
    private static final int MIN_HEART_RATE = 60;
    private static final int MAX_HEART_RATE = 100;
    private static final float MIN_TEMPERATURE = 36.1f;
    private static final float MAX_TEMPERATURE = 37.2f;

    public static boolean isHeartRateNormal(int heartRate) {
        return heartRate >= MIN_HEART_RATE && heartRate <= MAX_HEART_RATE;
    }

    public static boolean isTemperatureNormal(float temperature) {
        return temperature >= MIN_TEMPERATURE && temperature <= MAX_TEMPERATURE;
    }

    public static String getHeartRateStatus(int heartRate) {
        if (heartRate < MIN_HEART_RATE) return "Low";
        if (heartRate > MAX_HEART_RATE) return "High";
        return "Normal";
    }
}