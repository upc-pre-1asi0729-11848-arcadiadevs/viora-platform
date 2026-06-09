package com.arcadiadevs.viora.platform.agronomic.domain.model.queries;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AuthenticatedUserId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.TimeRange;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;

import java.util.Optional;

/**
 * Query to get agronomic statistics.
 *
 * <p>
 * This query allows retrieving agronomic statistics by user and time range.
 * It also supports an optional plot id to filter the statistics by a specific plot.
 * </p>
 *
 * @param userId The requested user id.
 * @param authenticatedUserId The authenticated user id.
 * @param plotId The optional plot id.
 * @param timeRange The selected time range.
 */
public record GetAgronomicStatisticsQuery(
        UserId userId,
        AuthenticatedUserId authenticatedUserId,
        Optional<PlotId> plotId,
        TimeRange timeRange
) {

    /**
     * Compact constructor for GetAgronomicStatisticsQuery.
     * Validates that required query data is not null.
     *
     * @throws IllegalArgumentException if userId, authenticatedUserId or timeRange is null.
     */
    public GetAgronomicStatisticsQuery {
        if (userId == null) {
            throw new IllegalArgumentException("User id is required.");
        }

        if (authenticatedUserId == null) {
            throw new IllegalArgumentException("Authenticated user id is required.");
        }

        if (timeRange == null) {
            throw new IllegalArgumentException("Time range is required.");
        }

        plotId = plotId == null ? Optional.empty() : plotId;
    }

    /**
     * Indicates whether the query is filtered by plot.
     *
     * @return true if plotId is present; otherwise false.
     */
    public boolean hasPlotId() {
        return plotId.isPresent();
    }
}