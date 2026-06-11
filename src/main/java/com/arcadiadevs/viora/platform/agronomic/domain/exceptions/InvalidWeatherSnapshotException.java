package com.arcadiadevs.viora.platform.agronomic.domain.exceptions;

/**
 * Exception thrown when a WeatherSnapshot violates domain rules.
 */
public class InvalidWeatherSnapshotException extends RuntimeException {

    public InvalidWeatherSnapshotException(String message) {
        super(message);
    }
}