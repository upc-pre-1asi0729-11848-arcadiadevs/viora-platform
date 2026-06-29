package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.assemblers;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.IoTDevice;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.entities.IoTDeviceEntity;

/**
 * Assembler that converts an IoTDevice domain aggregate
 * into an IoTDeviceEntity JPA persistence entity.
 * (boundedcontextfinal: infrastructure/persistence/jpa/assemblers)
 */
public final class IoTDeviceEntityFromIoTDeviceAssembler {

    private IoTDeviceEntityFromIoTDeviceAssembler() {}

    /**
     * Converts an IoTDevice domain aggregate to its JPA persistence entity.
     *
     * @param device the domain aggregate
     * @return the corresponding JPA entity
     */
    public static IoTDeviceEntity toEntityFromAggregate(IoTDevice device) {
        if (device == null) return null;

        var entity = new IoTDeviceEntity();
        if (device.getId() != null) {
            entity.setId(device.getId());
        }
        entity.setPlotId(device.getPlotId());
        entity.setDeviceName(device.getDeviceName());
        entity.setStatus(device.getStatus());
        entity.setActivationCode(device.getActivationCode() != null ? device.getActivationCode().value() : null);
        entity.setDeviceType(device.getDeviceType());
        return entity;
    }
}
