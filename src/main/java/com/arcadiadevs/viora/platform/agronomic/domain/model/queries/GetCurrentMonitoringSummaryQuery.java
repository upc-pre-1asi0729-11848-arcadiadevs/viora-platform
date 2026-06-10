package com.arcadiadevs.viora.platform.agronomic.domain.model.queries;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Query to retrieve the current monitoring summary for a specific user.
 *
 * <p>
 * This class transports the UserId parameter immutably, necessary to identify
 * the user requesting the summary.
 * </p>
 */
@Getter
@EqualsAndHashCode
public class GetCurrentMonitoringSummaryQuery {

    private final UserId userId;

    /**
     * Constructs a new GetCurrentMonitoringSummaryQuery.
     *
     * @param userId The identifier of the user for whom to retrieve the monitoring summary.
     */
    public GetCurrentMonitoringSummaryQuery(UserId userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null for GetCurrentMonitoringSummaryQuery.");
        }
        this.userId = userId;
    }
}