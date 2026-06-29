package com.arcadiadevs.viora.platform.agronomic.application.readmodels;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.IoTDevice;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeneralHealthStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.SensorReadings;

/**
 * Read model pairing an IoT device with its current (simulated) telemetry and the
 * health/alert level derived from it, so the query side can expose the operational
 * status (user-controlled) alongside the derived health.
 *
 * @param device   the persisted device aggregate
 * @param readings the simulated current readings
 * @param health   the health derived from the readings (Viora thresholds)
 */
public record IoTDeviceReadout(IoTDevice device, SensorReadings readings, GeneralHealthStatus health) {}
