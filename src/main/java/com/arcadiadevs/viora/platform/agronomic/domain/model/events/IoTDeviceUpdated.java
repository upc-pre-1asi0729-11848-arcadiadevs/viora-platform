package com.arcadiadevs.viora.platform.agronomic.domain.model.events;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.IoTDeviceStatus;
import org.jspecify.annotations.NullMarked;

/**
 * Domain event published when an IoT device is updated.
 *
 * @param deviceId the identifier of the updated device
 * @param plotId   the plot the device belongs to
 * @param oldStatus the status before the update
 * @param newStatus the status after the update
 */
@NullMarked
public record IoTDeviceUpdated(
        Long deviceId,
        Long plotId,
        IoTDeviceStatus oldStatus,
        IoTDeviceStatus newStatus
) {}
