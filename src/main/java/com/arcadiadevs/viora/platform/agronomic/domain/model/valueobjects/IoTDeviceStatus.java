package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

/**
 * Allowed operational states for an IoT device.
 */
public enum IoTDeviceStatus {
    ACTIVE,
    WARNING,
    CRITICAL,
    INACTIVE;

    public static IoTDeviceStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("IoTDeviceStatus is required");
        }
        try {
            return IoTDeviceStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid IoTDeviceStatus '%s'. Allowed: ACTIVE, WARNING, CRITICAL, INACTIVE".formatted(value));
        }
    }
}
