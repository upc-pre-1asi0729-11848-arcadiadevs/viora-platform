package com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.SatelliteImagery;

import java.util.Optional;

/**
 * Outbound port for obtaining satellite imagery from the configured provider.
 */
public interface AgroMonitoringImageryService {

    /**
     * Indicates whether the satellite imagery integration is configured and enabled.
     *
     * <p>
     * Used to derive integration link statuses for plots: when disabled, plots
     * cannot be linked to climate or satellite monitoring.
     * </p>
     *
     * @return True when the provider integration is enabled with credentials.
     */
    boolean isIntegrationEnabled();

    /**
     * Indicates whether the current plot boundary is registered with the provider.
     *
     * @param plot Plot whose integration state is requested.
     * @return True when the current boundary has a provider correlation.
     */
    boolean isPlotLinked(Plot plot);

    /**
     * Finds the most recent usable imagery for a plot.
     *
     * <p>Provider failures must degrade to an empty result so plot listing remains available.</p>
     *
     * @param plot Plot to inspect.
     * @return Latest imagery, or empty when unavailable.
     */
    Optional<SatelliteImagery> findCurrentImagery(Plot plot);

    /**
     * Fetches a raster NDVI tile of the current imagery cached for a plot.
     *
     * <p>
     * The provider credentials never leave the backend: the tile is fetched
     * server-side and returned as raw image bytes so clients can consume it
     * through the platform proxy endpoint.
     * </p>
     *
     * @param plot Plot whose current imagery tile is requested.
     * @param zoom Web-map zoom level.
     * @param x Tile column for the zoom level.
     * @param y Tile row for the zoom level.
     * @return Tile image bytes, or empty when no imagery is available.
     */
    Optional<byte[]> fetchCurrentNdviTile(Plot plot, int zoom, int x, int y);
}
