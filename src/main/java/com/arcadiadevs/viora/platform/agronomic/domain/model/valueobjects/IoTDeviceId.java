package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

/**
 * Value object that wraps the unique identifier of an IoT device.
 */
public record IoTDeviceId(Long value) {
    public IoTDeviceId {
        if (value == null || value <= 0)
            throw new IllegalArgumentException("IoTDeviceId must be a positive value");
    }
}
