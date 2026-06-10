package com.arcadiadevs.viora.platform.agronomic.application.readmodels;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.SatelliteImagery;

import java.util.Objects;
import java.util.Optional;

/**
 * Read model that combines plot data with its latest satellite imagery.
 *
 * @param plot Plot aggregate.
 * @param currentImagery Latest imagery when available.
 */
public record PlotWithCurrentImagery(
        Plot plot,
        Optional<SatelliteImagery> currentImagery
) {
    public PlotWithCurrentImagery {
        Objects.requireNonNull(plot, "Plot is required.");
        Objects.requireNonNull(currentImagery, "Current imagery optional is required.");
    }
}
