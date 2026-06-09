package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.IoTDevice;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.IoTDeviceResource;

/**
 * Assembler that converts an IoTDevice domain aggregate into an IoTDeviceResource DTO.
 * (TS12-005)
 */
public class IoTDeviceResourceFromIoTDeviceAssembler {

    private IoTDeviceResourceFromIoTDeviceAssembler() {}

    /**
     * Converts an IoTDevice aggregate to its REST resource representation.
     *
     * @param device the domain aggregate
     * @return the REST resource
     */
    public static IoTDeviceResource toResourceFromEntity(IoTDevice device) {

        return new IoTDeviceResource(
                device.getId(),
                device.getPlotId(),
                device.getDeviceName(),
                device.getStatus()
        );
    }
}