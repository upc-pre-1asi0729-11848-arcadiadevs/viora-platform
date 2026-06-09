package com.arcadiadevs.viora.platform.agronomic.domain.model.commands;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.IoTDeviceStatus;

/**
 * Command to register a new IoT device associated with a plot.
 * (TS13-002) When status is not provided, ACTIVE is assigned by default.
 *
 * @param plotId              the plot to associate the device with
 * @param authenticatedUserId the user performing the creation (must own the plot)
 * @param deviceName          the human-readable name of the device
 * @param status              the desired status; defaults to ACTIVE if null
 */
public record CreateIoTDeviceCommand(
        Long plotId,
        Long authenticatedUserId,
        String deviceName,
        IoTDeviceStatus status
) {
    public CreateIoTDeviceCommand {
        if (plotId == null)
            throw new IllegalArgumentException("CreateIoTDeviceCommand requires a valid plotId");
        if (authenticatedUserId == null)
            throw new IllegalArgumentException("CreateIoTDeviceCommand requires a valid authenticatedUserId");
        if (deviceName == null || deviceName.isBlank())
            throw new IllegalArgumentException("CreateIoTDeviceCommand requires a non-blank deviceName");
        if (status == null)
            status = IoTDeviceStatus.ACTIVE;
    }
}
