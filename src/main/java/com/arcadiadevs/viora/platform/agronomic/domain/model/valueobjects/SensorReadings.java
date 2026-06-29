package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import java.time.Instant;

/**
 * Current (simulated) telemetry of an IoT device. Metrics a device does not
 * report for its {@link IoTDeviceType} are {@code null}.
 *
 * @param soilMoisture    soil moisture percentage (0-100), or null
 * @param soilTemperature soil temperature in °C, or null
 * @param leafHumidity    leaf humidity percentage (0-100), or null
 * @param capturedAt      instant the reading was produced
 */
public record SensorReadings(
        Integer soilMoisture,
        Double soilTemperature,
        Integer leafHumidity,
        Instant capturedAt
) {}
