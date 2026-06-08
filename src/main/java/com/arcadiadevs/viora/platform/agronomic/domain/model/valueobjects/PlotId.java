package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

/**
 * Value object that wraps the unique identifier of a Plot.
 */
public record PlotId(Long value) {
    public PlotId {
        if (value == null || value <= 0)
            throw new IllegalArgumentException("PlotId must be a positive value");
    }
}
