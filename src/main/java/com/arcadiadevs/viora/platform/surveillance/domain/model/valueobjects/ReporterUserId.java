package com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects;

/**
 * Identifier for the user reporting the pest sighting.
 */
public record ReporterUserId(Long value) {
    public ReporterUserId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("ReporterUserId must be greater than zero");
        }
    }
}
