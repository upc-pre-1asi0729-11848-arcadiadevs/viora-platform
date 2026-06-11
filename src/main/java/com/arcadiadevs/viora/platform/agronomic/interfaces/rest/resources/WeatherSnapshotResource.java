package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

import java.time.LocalDate;

/**
 * Resource for WeatherSnapshot.
 */
public record WeatherSnapshotResource(
        String weatherStatus,
        LocalDate measurementDate,
        String climateRiskLevel,
        Double temperature // New field
) {
}