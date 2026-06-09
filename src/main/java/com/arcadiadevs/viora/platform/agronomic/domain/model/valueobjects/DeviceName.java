package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

/**
 * Value object representing the human-readable name of an IoT device.
 */
public record DeviceName(String value) {
    public DeviceName {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("DeviceName must not be blank");
        if (value.length() > 150)
            throw new IllegalArgumentException("DeviceName must not exceed 150 characters");
    }
}
