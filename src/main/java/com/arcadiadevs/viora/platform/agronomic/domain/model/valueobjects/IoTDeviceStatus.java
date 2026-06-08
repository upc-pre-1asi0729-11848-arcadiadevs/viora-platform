package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

/**
 * Allowed operational states for an IoT device.
 */
public enum IoTDeviceStatus {
    ACTIVE,
    INACTIVE,
    MAINTENANCE;

    public static IoTDeviceStatus fromString(String value) {
        try {
            return IoTDeviceStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid IoTDeviceStatus '%s'. Allowed: ACTIVE, INACTIVE, MAINTENANCE".formatted(value));
        }
    }
}
