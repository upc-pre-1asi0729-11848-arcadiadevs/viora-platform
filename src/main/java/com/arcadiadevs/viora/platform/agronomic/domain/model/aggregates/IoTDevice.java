package com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DeviceName;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.IoTDeviceStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

/**
 * IoTDevice aggregate root.
 *
 * <p>
 * Represents a sensor or IoT device associated with a plot.
 * Invariants:
 * <ul>
 *   <li>Plot association is mandatory.</li>
 *   <li>Device name must not be blank.</li>
 *   <li>Status must be a valid IoTDeviceStatus value.</li>
 * </ul>
 * </p>
 */
@Getter
public class IoTDevice extends AbstractDomainAggregateRoot<IoTDevice> {

    @Setter
    private Long id;

    @Setter
    private Long plotId;

    @Setter
    private String deviceName;

    @Setter
    private IoTDeviceStatus status;

    /**
     * Creates a new IoTDevice from a PlotId, DeviceName and IoTDeviceStatus.
     *
     * @param plotId     the plot this device belongs to
     * @param deviceName the human-readable name of the device
     * @param status     the operational status
     */
    public IoTDevice(PlotId plotId, DeviceName deviceName, IoTDeviceStatus status) {
        if (plotId == null)
            throw new IllegalArgumentException("IoTDevice requires a valid PlotId");
        this.plotId = plotId.getValue();
        this.deviceName = deviceName.value();
        this.status = status;
    }
}
