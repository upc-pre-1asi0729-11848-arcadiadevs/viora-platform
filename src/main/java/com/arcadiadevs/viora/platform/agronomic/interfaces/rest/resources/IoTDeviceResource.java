package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.IoTDeviceStatus;

/**
 * REST response resource for IoTDevice.
 * (TS12-005) Used by GET /api/v1/plots/{plotId}/iot-devices.
 *
 * @param id         the device identifier
 * @param plotId     the associated plot identifier
 * @param deviceName the human-readable device name
 * @param status     the operational status of the device
 */
public record IoTDeviceResource(
        Long id,
        Long plotId,
        String deviceName,
        IoTDeviceStatus status
) {}
