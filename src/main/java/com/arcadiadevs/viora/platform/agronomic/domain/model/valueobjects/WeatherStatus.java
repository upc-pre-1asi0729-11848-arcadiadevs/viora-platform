package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

/**
 * Represents the weather conditions at a specific time.
 */
public enum WeatherStatus {
    SUNNY,
    CLOUDY,
    RAINY,
    STORMY,
    SNOWY,
    UNKNOWN;

    public static WeatherStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("WeatherStatus is required");
        }
        try {
            return WeatherStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid WeatherStatus '%s'. Allowed: SUNNY, CLOUDY, RAINY, STORMY, SNOWY, UNKNOWN".formatted(value));
        }
    }
}