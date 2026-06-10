package com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.SatelliteImagery;

import java.util.Optional;

/**
 * Outbound port for obtaining satellite imagery from the configured provider.
 */
public interface AgroMonitoringImageryService {

    /**
     * Finds the most recent usable imagery for a plot.
     *
     * <p>Provider failures must degrade to an empty result so plot listing remains available.</p>
     *
     * @param plot Plot to inspect.
     * @return Latest imagery, or empty when unavailable.
     */
    Optional<SatelliteImagery> findCurrentImagery(Plot plot);
}
