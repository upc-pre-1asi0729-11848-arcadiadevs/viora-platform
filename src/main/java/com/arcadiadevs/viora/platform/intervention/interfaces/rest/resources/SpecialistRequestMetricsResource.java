package com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources;

/**
 * Resource representing the intervention request metrics for a specialist.
 */
public record SpecialistRequestMetricsResource(
        Integer pendingCount,
        Integer underEvaluationCount,
        Integer proposalsCount
) {
}
