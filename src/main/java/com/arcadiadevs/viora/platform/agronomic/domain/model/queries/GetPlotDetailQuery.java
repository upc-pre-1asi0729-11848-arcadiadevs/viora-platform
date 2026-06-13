package com.arcadiadevs.viora.platform.agronomic.domain.model.queries;

/**
 * Query for the configuration and monitoring detail of one plot.
 *
 * @param userId Owner user identifier.
 * @param plotId Plot identifier.
 */
public record GetPlotDetailQuery(Long userId, Long plotId) {

    public GetPlotDetailQuery {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number.");
        }
        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException("Plot ID must be a positive number.");
        }
    }
}
