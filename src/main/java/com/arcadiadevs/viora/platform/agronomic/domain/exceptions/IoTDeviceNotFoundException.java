package com.arcadiadevs.viora.platform.agronomic.domain.exceptions;

/**
 * Thrown when an IoT device cannot be found by the given identifier.
 */
public class IoTDeviceNotFoundException extends RuntimeException {

    public IoTDeviceNotFoundException(Long id) {
        super("IoT device with id %d not found".formatted(id));
    }
}
