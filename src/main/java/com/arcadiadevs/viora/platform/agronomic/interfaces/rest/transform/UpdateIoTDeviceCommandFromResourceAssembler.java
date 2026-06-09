package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.agronomic.application.commands.UpdateIoTDeviceCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DeviceName;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.IoTDeviceStatus;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.UpdateIoTDeviceResource;
import org.jspecify.annotations.NullMarked;

/**
 * Assembler that transforms an UpdateIoTDeviceResource into an UpdateIoTDeviceCommand.
 */
@NullMarked
public final class UpdateIoTDeviceCommandFromResourceAssembler {

    private UpdateIoTDeviceCommandFromResourceAssembler() {}

    /**
     * Converts a REST resource plus path variables and auth context into a command.
     *
     * @param resource          the PATCH request body
     * @param plotId            the plot identifier from the path
     * @param deviceId          the device identifier from the path
     * @param authenticatedUserId the authenticated user identifier
     * @return the assembled command
     */
    public static UpdateIoTDeviceCommand toCommand(
            UpdateIoTDeviceResource resource,
            Long plotId,
            Long deviceId,
            Long authenticatedUserId) {

        return new UpdateIoTDeviceCommand(
                plotId,
                deviceId,
                authenticatedUserId,
                new DeviceName(resource.deviceName()),
                IoTDeviceStatus.fromString(resource.iotDeviceStatus())
        );
    }
}
