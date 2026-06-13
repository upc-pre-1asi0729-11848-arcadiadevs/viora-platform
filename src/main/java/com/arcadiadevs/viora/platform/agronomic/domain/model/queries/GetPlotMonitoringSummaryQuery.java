package com.arcadiadevs.viora.platform.agronomic.domain.model.queries;

/**
 * Query for the real-time monitoring summary of a single plot.
 *
 * @param userId Owner user identifier.
 * @param plotId Plot identifier.
 */
public record GetPlotMonitoringSummaryQuery(Long userId, Long plotId) {

    public GetPlotMonitoringSummaryQuery {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number.");
        }
        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException("Plot ID must be a positive number.");
        }
    }
}
