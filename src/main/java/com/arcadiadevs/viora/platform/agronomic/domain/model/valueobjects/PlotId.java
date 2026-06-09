package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

/**
 * Value object representing the plot id.
 *
 * <p>
 * This value object is used to link agronomic data to a plot.
 * It must be a positive Long value.
 * </p>
 *
 * @param plotId The plot id. It cannot be null or less than 1.
 */
public record PlotId(Long plotId) {

    /**
     * Compact constructor for PlotId.
     * Validates that the plotId is not null and is greater than or equal to 1.
     *
     * @throws IllegalArgumentException if the plotId is null or less than 1.
     */
    public PlotId {
        if (plotId == null || plotId < 1) {
            throw new IllegalArgumentException("Plot id cannot be null or less than 1");
        }
    }
}