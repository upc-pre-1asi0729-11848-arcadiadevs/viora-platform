package com.arcadiadevs.viora.platform.agronomic.domain.model.commands;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.IoTDeviceStatus;

/**
 * Command to register a new IoT device associated with a plot.
 * (TS13-002) When status is not provided, ACTIVE is assigned by default.
 *
 * @param plotId      the plot to associate the device with
 * @param deviceName  the human-readable name of the device
 * @param status      the desired status; defaults to ACTIVE if null
 */
public record CreateIoTDeviceCommand(
        Long plotId,
        String deviceName,
        IoTDeviceStatus status
) {
    public CreateIoTDeviceCommand {
        if (plotId == null || plotId <= 0)
            throw new IllegalArgumentException("CreateIoTDeviceCommand requires a valid plotId");
        if (deviceName == null || deviceName.isBlank())
            throw new IllegalArgumentException("CreateIoTDeviceCommand requires a non-blank deviceName");
        if (status == null)
            status = IoTDeviceStatus.ACTIVE;
    }
}
