package com.arcadiadevs.viora.platform.agronomic.application.readmodels;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.IoTDevice;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.SensorReadings;

/**
 * Read model pairing an IoT device with its current (simulated) telemetry, so the
 * query side can expose the device metadata together with live sensor values.
 *
 * @param device   the persisted device aggregate
 * @param readings the simulated current readings
 */
public record IoTDeviceReadout(IoTDevice device, SensorReadings readings) {}
