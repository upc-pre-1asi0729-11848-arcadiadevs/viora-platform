package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.CreateIoTDeviceCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.IoTDeviceStatus;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.CreateIoTDeviceResource;

/**
 * Static assembler that converts a {@link CreateIoTDeviceResource} REST request body
 * into a {@link CreateIoTDeviceCommand} application command.
 * (TS13-004)
 */
public final class CreateIoTDeviceCommandFromResourceAssembler {

    private CreateIoTDeviceCommandFromResourceAssembler() {}

    /**
     * Converts a REST resource to the corresponding application command.
     *
     * @param resource the incoming REST request body
     * @param plotId   the plot identifier extracted from the URL path variable
     * @return the application command ready to be handled by the command service
     */
    public static CreateIoTDeviceCommand toCommandFromResource(
            CreateIoTDeviceResource resource, Long plotId) {
        IoTDeviceStatus effectiveStatus =
                resource.status() != null ? resource.status() : IoTDeviceStatus.ACTIVE;

        return new CreateIoTDeviceCommand(
                plotId,
                resource.userId(),
                resource.deviceName(),
                effectiveStatus
        );
    }
}
