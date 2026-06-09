package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * PlotName value object.
 * <p>
 *     Represents the bussines name assigned by the grower to a plot
 * </p>
 */
@Getter
@EqualsAndHashCode
public class PlotName {
    /**
     * The plot name.
     */

    private final String value;

    /**
     * Constructor for PlotName
     * @param value The plot name
     */
    public PlotName(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Plot name cannot be empty.");
        }

        if (value.trim().length() < 3) {
            throw new IllegalArgumentException("Plot name must have at least 3 characters.");
        }

        if (value.trim().length() > 80) {
            throw new IllegalArgumentException("Plot name cannot exceed 80 characters.");
        }

        this.value = value.trim();
    }
}
