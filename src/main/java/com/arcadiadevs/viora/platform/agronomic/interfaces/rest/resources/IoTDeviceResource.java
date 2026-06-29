package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.IoTDeviceStatus;

/**
 * REST response resource for IoTDevice, including current (simulated) telemetry.
 * (TS12-005) Used by the IoT device read endpoints.
 *
 * @param id           the device identifier
 * @param plotId       the associated plot identifier
 * @param deviceName   the human-readable device name
 * @param status       the operational status (user-controlled: ACTIVE | INACTIVE)
 * @param health       the derived health/alert level (HEALTHY | WARNING | CRITICAL | UNKNOWN), or null
 * @param deviceType   the sensor kind (SOIL_PROBE | LEAF_WETNESS | WEATHER_STATION), or null
 * @param soilMoisture current soil moisture percentage, or null if not reported
 * @param temperature  current soil temperature in °C, or null if not reported
 * @param leafHumidity current leaf humidity percentage, or null if not reported
 * @param lastUpdate   ISO-8601 instant of the latest reading, or null
 */
public record IoTDeviceResource(
        Long id,
        Long plotId,
        String deviceName,
        IoTDeviceStatus status,
        String health,
        String deviceType,
        Integer soilMoisture,
        Double temperature,
        Integer leafHumidity,
        String lastUpdate
) {}
