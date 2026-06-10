package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.NullMarked;

/**
 * PATCH request body for updating an IoT device.
 *
 * @param deviceName       the new device name
 * @param iotDeviceStatus  the new operational status
 */
@NullMarked
public record UpdateIoTDeviceResource(
        @NotBlank String deviceName,
        @NotBlank String iotDeviceStatus
) {}
