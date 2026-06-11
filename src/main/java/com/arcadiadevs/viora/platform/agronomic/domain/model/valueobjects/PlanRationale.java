package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Represents the agronomic justification behind a dynamic nutrition plan.
 *
 * <p>
 * Captures the climate risk that triggered the plan, the consolidated NDVI
 * observed and the temperature anomaly against the seasonal baseline.
 * </p>
 */
@Getter
@EqualsAndHashCode
public class PlanRationale {

    private static final int SUMMARY_MAX_LENGTH = 500;

    private final String summary;
    private final ClimateRiskLevel triggeringRiskLevel;
    private final NdviValue ndviValue;
    private final Double temperatureAnomaly;

    public PlanRationale(
            String summary,
            ClimateRiskLevel triggeringRiskLevel,
            NdviValue ndviValue,
            Double temperatureAnomaly
    ) {
        validateRequiredFields(summary, triggeringRiskLevel, ndviValue, temperatureAnomaly);

        this.summary = summary.trim();
        this.triggeringRiskLevel = triggeringRiskLevel;
        this.ndviValue = ndviValue;
        this.temperatureAnomaly = temperatureAnomaly;
    }

    private void validateRequiredFields(
            String summary,
            ClimateRiskLevel triggeringRiskLevel,
            NdviValue ndviValue,
            Double temperatureAnomaly
    ) {
        if (summary == null || summary.isBlank()) {
            throw new IllegalArgumentException("Plan rationale summary is required.");
        }
        if (summary.trim().length() > SUMMARY_MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "Plan rationale summary cannot exceed %d characters.".formatted(SUMMARY_MAX_LENGTH));
        }
        if (triggeringRiskLevel == null) {
            throw new IllegalArgumentException("Triggering climate risk level is required.");
        }
        if (ndviValue == null) {
            throw new IllegalArgumentException("NDVI value is required.");
        }
        if (temperatureAnomaly == null) {
            throw new IllegalArgumentException("Temperature anomaly is required.");
        }
    }
}
