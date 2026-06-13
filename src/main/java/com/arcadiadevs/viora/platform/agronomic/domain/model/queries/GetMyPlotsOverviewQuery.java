package com.arcadiadevs.viora.platform.agronomic.domain.model.queries;

/**
 * Query to retrieve the My Plots monitoring overview for a user.
 *
 * @param userId The owner user identifier.
 */
public record GetMyPlotsOverviewQuery(Long userId) {

    public GetMyPlotsOverviewQuery {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number.");
        }
    }
}
