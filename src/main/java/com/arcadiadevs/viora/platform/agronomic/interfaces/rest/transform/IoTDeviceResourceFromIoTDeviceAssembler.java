package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.agronomic.application.readmodels.IoTDeviceReadout;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.IoTDevice;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.SensorReadings;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.IoTDeviceResource;

/**
 * Assembler that converts IoTDevice aggregates (and their telemetry readouts)
 * into IoTDeviceResource DTOs.
 * (TS12-005)
 */
public class IoTDeviceResourceFromIoTDeviceAssembler {

    private IoTDeviceResourceFromIoTDeviceAssembler() {}

    /**
     * Converts a device readout (device + current telemetry) to its REST resource.
     *
     * @param readout the device with its simulated readings
     * @return the REST resource including telemetry
     */
    public static IoTDeviceResource toResourceFromReadout(IoTDeviceReadout readout) {
        IoTDevice device = readout.device();
        SensorReadings readings = readout.readings();

        return new IoTDeviceResource(
                device.getId(),
                device.getPlotId(),
                device.getDeviceName(),
                device.getStatus(),
                device.getDeviceType() != null ? device.getDeviceType().name() : null,
                readings != null ? readings.soilMoisture() : null,
                readings != null ? readings.soilTemperature() : null,
                readings != null ? readings.leafHumidity() : null,
                readings != null && readings.capturedAt() != null ? readings.capturedAt().toString() : null
        );
    }

    /**
     * Converts a device aggregate (without telemetry) to its REST resource, used
     * for write responses where readings are not yet relevant.
     *
     * @param device the domain aggregate
     * @return the REST resource with null telemetry
     */
    public static IoTDeviceResource toResourceFromEntity(IoTDevice device) {
        return new IoTDeviceResource(
                device.getId(),
                device.getPlotId(),
                device.getDeviceName(),
                device.getStatus(),
                device.getDeviceType() != null ? device.getDeviceType().name() : null,
                null,
                null,
                null,
                null
        );
    }
}
