package com.arcadiadevs.viora.platform.agronomic.domain.model.services;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeneralHealthStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.SensorReadings;
import org.springframework.stereotype.Service;

/**
 * Domain service that derives a device's health/alert level from its telemetry.
 *
 * <p>
 * The alert level is Viora's responsibility, not a manual choice: a device's
 * operational {@code status} (ACTIVE/INACTIVE) says whether the sensor is enabled,
 * while this evaluator computes whether its current readings are healthy. Each
 * reported metric is scored against the same thresholds the dashboard cards use,
 * and the worst metric wins. A device with no readings is {@code UNKNOWN}.
 * </p>
 */
@Service
public class SensorHealthEvaluator {

    /**
     * Derives the health status implied by a device's current readings.
     *
     * @param readings the current (simulated) telemetry; may be null
     * @return the worst severity across reported metrics, UNKNOWN when none exist
     */
    public GeneralHealthStatus evaluate(SensorReadings readings) {
        if (readings == null) {
            return GeneralHealthStatus.UNKNOWN;
        }

        int severity = -1;
        severity = Math.max(severity, soilMoistureSeverity(readings.soilMoisture()));
        severity = Math.max(severity, soilTemperatureSeverity(readings.soilTemperature()));
        severity = Math.max(severity, leafHumiditySeverity(readings.leafHumidity()));

        return switch (severity) {
            case 2 -> GeneralHealthStatus.CRITICAL;
            case 1 -> GeneralHealthStatus.WARNING;
            case 0 -> GeneralHealthStatus.HEALTHY;
            default -> GeneralHealthStatus.UNKNOWN;
        };
    }

    /** Dry soil is the risky end: below the wilting threshold is critical. */
    private int soilMoistureSeverity(Integer soilMoisture) {
        if (soilMoisture == null) {
            return -1;
        }
        if (soilMoisture < 20) {
            return 2;
        }
        return soilMoisture <= 35 ? 1 : 0;
    }

    /** Hot soil compounds water stress. */
    private int soilTemperatureSeverity(Double soilTemperature) {
        if (soilTemperature == null) {
            return -1;
        }
        if (soilTemperature > 30.0) {
            return 2;
        }
        return soilTemperature >= 25.0 ? 1 : 0;
    }

    /** Prolonged canopy wetness is the risky end (fungal disease). */
    private int leafHumiditySeverity(Integer leafHumidity) {
        if (leafHumidity == null) {
            return -1;
        }
        if (leafHumidity > 85) {
            return 2;
        }
        return leafHumidity >= 70 ? 1 : 0;
    }
}
