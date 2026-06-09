package com.arcadiadevs.viora.platform.agronomic.domain.model.queries;

/**
 * GetPlotsByUserId query.
 *
 * <p>
 * Represents the intention of retrieving all productive agricultural plots 
 * associated with a specific owner user.
 * </p>
 *
 * @param userId The unique identifier of the owner user.
 */
public record GetPlotsByUserIdQuery(Long userId) {
    /**
     * Compact constructor for GetPlotsByUserIdQuery.
     *
     * <p>
     * Validates that the user identifier is present and positive before the query
     * is processed by the application layer.
     * </p>
     */
    public GetPlotsByUserIdQuery {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number.");
        }
    }
}