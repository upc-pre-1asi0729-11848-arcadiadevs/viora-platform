package com.arcadiadevs.viora.platform.agronomic.domain.model.queries;

/**
 * Query for active plots enriched with their latest satellite imagery.
 *
 * @param userId Owner user identifier.
 */
public record GetPlotsWithCurrentImageryQuery(Long userId) {
    public GetPlotsWithCurrentImageryQuery {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number.");
        }
    }
}
