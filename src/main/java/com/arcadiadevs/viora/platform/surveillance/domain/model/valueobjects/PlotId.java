package com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects;

/**
 * Plot Identifier within the surveillance bounded context.
 */
public record PlotId(Long value) {
    public PlotId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("PlotId must be greater than zero");
        }
    }
}
