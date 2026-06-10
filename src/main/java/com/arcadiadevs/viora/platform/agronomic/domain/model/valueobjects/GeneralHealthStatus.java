package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

/**
 * Represents the general health status of a monitoring summary.
 */
public enum GeneralHealthStatus {
    HEALTHY,
    WARNING,
    CRITICAL,
    UNKNOWN;

    public static GeneralHealthStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("GeneralHealthStatus is required");
        }
        try {
            return GeneralHealthStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid GeneralHealthStatus '%s'. Allowed: HEALTHY, WARNING, CRITICAL, UNKNOWN".formatted(value));
        }
    }
}