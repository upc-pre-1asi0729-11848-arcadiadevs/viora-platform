package com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates;

import com.arcadiadevs.viora.platform.agronomic.domain.model.events.IoTDeviceUpdated;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ActivationCode;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DeviceName;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.IoTDeviceStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.IoTDeviceType;
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
     * The one-time activation (claim) code the producer used to register the
     * device. Doubles as the device serial and the simulation seed. May be null
     * for legacy rows created before activation codes existed.
     */
    @Setter
    private ActivationCode activationCode;

    /**
     * The kind of sensor, derived from the activation code; decides which metrics
     * the device reports. May be null for legacy rows.
     */
    @Setter
    private IoTDeviceType deviceType;

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

    /**
     * Claims a physical device for a plot using its activation code. The sensor
     * kind is derived from the code prefix.
     *
     * @param plotId     the plot this device belongs to
     * @param deviceName the human-readable name of the device
     * @param status     the initial operational status
     * @param code       the validated, issued activation code
     * @return the claimed device aggregate
     */
    public static IoTDevice claim(PlotId plotId, DeviceName deviceName, IoTDeviceStatus status, ActivationCode code) {
        if (code == null)
            throw new IllegalArgumentException("IoTDevice requires an activation code to be claimed");
        var device = new IoTDevice(plotId, deviceName, status);
        device.activationCode = code;
        device.deviceType = code.deviceType();
        return device;
    }

    /**
     * Updates the device name and status, and registers an IoTDeviceUpdated domain event.
     *
     * @param newName  the new device name
     * @param newStatus the new operational status
     */
    public void update(DeviceName newName, IoTDeviceStatus newStatus) {
        IoTDeviceStatus oldStatus = this.status;
        this.deviceName = newName.value();
        this.status = newStatus;
        registerDomainEvent(new IoTDeviceUpdated(this.id, this.plotId, oldStatus, newStatus));
    }
}
