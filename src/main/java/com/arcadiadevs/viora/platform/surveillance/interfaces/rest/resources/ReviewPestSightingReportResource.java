package com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources;

/**
 * Request body to resolve a pest sighting report after a field inspection.
 *
 * @param outcome The inspection outcome: {@code CONFIRMED} (threat verified) or
 *                {@code RULED_OUT} (verified false positive).
 */
public record ReviewPestSightingReportResource(
        String outcome
) {
}
