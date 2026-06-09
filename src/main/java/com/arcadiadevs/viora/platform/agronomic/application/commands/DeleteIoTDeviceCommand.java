package com.arcadiadevs.viora.platform.agronomic.application.commands;

import org.jspecify.annotations.NullMarked;

/**
 * Command to delete an IoT device scoped to a plot.
 *
 * @param plotId              the plot the device belongs to
 * @param deviceId            the device to delete
 * @param authenticatedUserId the caller identity for ownership check
 */
@NullMarked
public record DeleteIoTDeviceCommand(
        Long plotId,
        Long deviceId,
        Long authenticatedUserId
) {}
