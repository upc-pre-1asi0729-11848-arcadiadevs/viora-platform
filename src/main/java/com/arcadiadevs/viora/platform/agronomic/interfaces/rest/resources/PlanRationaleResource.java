package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

/**
 * Resource for PlanRationale.
 */
public record PlanRationaleResource(
        String summary,
        String triggeringRiskLevel,
        Double ndviValue,
        Double temperatureAnomaly
) {
}
