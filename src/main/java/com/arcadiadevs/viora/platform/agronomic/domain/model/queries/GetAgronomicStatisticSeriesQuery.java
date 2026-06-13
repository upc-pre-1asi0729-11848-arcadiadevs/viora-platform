package com.arcadiadevs.viora.platform.agronomic.domain.model.queries;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.TimeRange;

/**
 * Query for an aggregated agronomic statistic series with period-over-period trends.
 *
 * @param userId The user whose statistics are charted.
 * @param authenticatedUserId The authenticated user identifier.
 * @param plotId Optional plot filter; null aggregates across the user's plots.
 * @param timeRange The selected time range (7d, 30d, campaign, ...).
 */
public record GetAgronomicStatisticSeriesQuery(
        Long userId,
        Long authenticatedUserId,
        Long plotId,
        TimeRange timeRange
) {

    public GetAgronomicStatisticSeriesQuery {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number.");
        }
        if (authenticatedUserId == null || authenticatedUserId <= 0) {
            throw new IllegalArgumentException("Authenticated user ID must be a positive number.");
        }
        if (plotId != null && plotId <= 0) {
            throw new IllegalArgumentException("Plot ID must be a positive number.");
        }
        if (timeRange == null) {
            throw new IllegalArgumentException("Time range is required.");
        }
    }
}
