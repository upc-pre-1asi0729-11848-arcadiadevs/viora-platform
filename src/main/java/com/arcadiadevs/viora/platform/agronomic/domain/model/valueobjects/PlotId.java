package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Plot identifier value object.
 *
 * <p>
 * Represents the unique identifier of a plot in the agronomic bounded context.
 * </p>
 */
@Getter
@EqualsAndHashCode
public class PlotId {

    /**
     * The raw numeric identifier.
     */
    private final Long value;

    /**
     * Creates a plot identifier.
     *
     * @param value The plot identifier.
     */
    public PlotId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Plot ID must be a positive number.");
        }
        this.value = value;
    }
}
