package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.IoTDeviceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * REST request body for creating a new IoT device.
 * (TS13-004) Used by POST /api/v1/plots/{plotId}/iot-devices.
 *
 * @param userId     the authenticated user identifier
 * @param deviceName the human-readable name of the device (required)
 * @param status     the desired status; ACTIVE by default when omitted
 */
public record CreateIoTDeviceResource(

        @NotNull(message = "userId is required")
        Long userId,

        @NotBlank(message = "deviceName is required")
        @Size(max = 150, message = "deviceName must not exceed 150 characters")
        String deviceName,

        IoTDeviceStatus status
) {}
