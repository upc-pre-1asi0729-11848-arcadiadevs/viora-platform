package com.arcadiadevs.viora.platform.agronomic.domain.model.commands;

/**
 * DeleteIoTDevice command.
 *
 * <p>
 *     Represents the intention of deleting an IoT device from a plot.
 * </p>
 *
 * @param plotId   The identifier of the plot that owns the device.
 * @param deviceId The identifier of the IoT device to delete.
 */
public record DeleteIoTDeviceCommand(Long plotId, Long deviceId) {

    /**
     * Compact constructor for DeleteIoTDeviceCommand.
     */
    public DeleteIoTDeviceCommand {
        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException("Plot ID must be a positive number.");
        }
        if (deviceId == null || deviceId <= 0) {
            throw new IllegalArgumentException("Device ID must be a positive number.");
        }
    }
}
