package com.arcadiadevs.viora.platform.intervention.domain.model.queries;

/**
 * Query to get the intervention request metrics for a specific grower.
 */
public record GetGrowerRequestMetricsQuery(Long growerId) {
}
