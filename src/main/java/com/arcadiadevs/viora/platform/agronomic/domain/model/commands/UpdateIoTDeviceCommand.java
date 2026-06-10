package com.arcadiadevs.viora.platform.agronomic.domain.model.commands;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.IoTDeviceStatus;

/**
 * Command to update an IoT device's metadata scoped to a plot.
 *
 * @param plotId      the plot the device belongs to
 * @param deviceId    the device to update
 * @param deviceName  the new name for the device
 * @param status      the new operational status
 */
public record UpdateIoTDeviceCommand(
        Long plotId,
        Long deviceId,
        String deviceName,
        IoTDeviceStatus status
) {
    public UpdateIoTDeviceCommand {
        if (plotId == null)
            throw new IllegalArgumentException("UpdateIoTDeviceCommand requires a valid plotId");
        if (deviceId == null)
            throw new IllegalArgumentException("UpdateIoTDeviceCommand requires a valid deviceId");
        if (deviceName == null || deviceName.isBlank())
            throw new IllegalArgumentException("UpdateIoTDeviceCommand requires a non-blank deviceName");
        if (status == null)
            throw new IllegalArgumentException("UpdateIoTDeviceCommand requires a valid status");
    }
}
