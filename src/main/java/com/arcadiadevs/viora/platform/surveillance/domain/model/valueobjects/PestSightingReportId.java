package com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects;

/**
 * Pest Sighting Report Identifier.
 */
public record PestSightingReportId(Long value) {
    public PestSightingReportId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("PestSightingReportId must be greater than zero");
        }
    }
}
