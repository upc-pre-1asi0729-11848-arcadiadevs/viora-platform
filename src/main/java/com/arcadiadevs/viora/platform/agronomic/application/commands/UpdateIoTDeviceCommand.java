package com.arcadiadevs.viora.platform.agronomic.application.commands;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DeviceName;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.IoTDeviceStatus;
import org.jspecify.annotations.NullMarked;

/**
 * Command to update an IoT device's metadata scoped to a plot.
 *
 * @param plotId              the plot the device belongs to
 * @param deviceId            the device to update
 * @param authenticatedUserId the caller identity for ownership check
 * @param deviceName          the new name for the device
 * @param iotDeviceStatus     the new operational status
 */
@NullMarked
public record UpdateIoTDeviceCommand(
        Long plotId,
        Long deviceId,
        Long authenticatedUserId,
        DeviceName deviceName,
        IoTDeviceStatus iotDeviceStatus
) {}
