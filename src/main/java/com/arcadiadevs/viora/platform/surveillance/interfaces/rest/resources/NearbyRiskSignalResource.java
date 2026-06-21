package com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources;

/**
 * Anonymized nearby risk signal shown on the Community Risk section.
 *
 * @param id             Synthetic identifier (does not reveal the source alert).
 * @param title          Anonymized, human-readable headline for the signal.
 * @param probableThreat The probable threat behind the signal.
 * @param severity       Severity of the signal (LOW | MEDIUM | HIGH | CRITICAL).
 * @param distanceKm     Approximate distance from the reference plot, in kilometers.
 */
public record NearbyRiskSignalResource(
        String id,
        String title,
        String probableThreat,
        String severity,
        double distanceKm
) {
}
