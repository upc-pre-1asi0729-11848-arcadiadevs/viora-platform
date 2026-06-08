package com.arcadiadevs.viora.platform.agronomic.domain.model.queries;

/**
 * GetPlotById query.
 *
 * <p>
 *     Represents the intention of retrieving a specific plot by its unique identifier.
 * </p>
 *
 * @param plotId The unique identifier of the plot.
 */
public record GetPlotByIdQuery(Long plotId) {
    /**
     * Compact constructor for GetPlotByIdQuery.
     *
     * <p>
     *     Validates that the plot identifier is present and positive before the query
     *     is processed by the application layer.
     * </p>
     */
    public GetPlotByIdQuery {
        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException("Plot ID must be a positive number.");
        }
    }
}