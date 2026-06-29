package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.assemblers;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.IoTDevice;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ActivationCode;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DeviceName;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.entities.IoTDeviceEntity;

/**
 * Assembler that converts an IoTDeviceEntity JPA persistence entity
 * into an IoTDevice domain aggregate.
 * (boundedcontextfinal: infrastructure/persistence/jpa/assemblers)
 */
public final class IoTDeviceFromIoTDeviceEntityAssembler {

    private IoTDeviceFromIoTDeviceEntityAssembler() {}

    /**
     * Converts a JPA entity to the IoTDevice domain aggregate.
     *
     * @param entity the JPA entity
     * @return the corresponding domain aggregate
     */
    public static IoTDevice toAggregateFromEntity(IoTDeviceEntity entity) {
        if (entity == null) return null;

        var device = new IoTDevice(
                new PlotId(entity.getPlotId()),
                new DeviceName(entity.getDeviceName()),
                entity.getStatus()
        );
        device.setId(entity.getId());
        if (entity.getActivationCode() != null) {
            device.setActivationCode(new ActivationCode(entity.getActivationCode()));
        }
        device.setDeviceType(entity.getDeviceType());
        return device;
    }
}
