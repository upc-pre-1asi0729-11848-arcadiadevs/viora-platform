package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.UpdateIoTDeviceCommand;
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
     * Converts a REST resource plus path variables into a command.
     *
     * @param resource the PATCH request body
     * @param plotId   the plot identifier from the path
     * @param deviceId the device identifier from the path
     * @return the assembled command
     */
    public static UpdateIoTDeviceCommand toCommandFromResource(
            UpdateIoTDeviceResource resource,
            Long plotId,
            Long deviceId) {

        return new UpdateIoTDeviceCommand(
                plotId,
                deviceId,
                resource.deviceName(),
                IoTDeviceStatus.fromString(resource.iotDeviceStatus())
        );
    }
}
