package com.arcadiadevs.viora.platform.agronomic.domain.model.queries;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.TimeRange;

/**
 * GetAgronomicStatistics query.
 *
 * <p>
 * Represents the intention of retrieving agronomic statistics
 * for a user, optionally filtered by plot and constrained by a time range.
 * </p>
 *
 * @param userId The user identifier whose statistics will be queried.
 * @param authenticatedUserId The authenticated user identifier.
 * @param plotId The optional plot identifier.
 * @param timeRange The selected time range.
 */
public record GetAgronomicStatisticsQuery(
        Long userId,
        Long authenticatedUserId,
        Long plotId,
        TimeRange timeRange
) {

    public GetAgronomicStatisticsQuery {
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