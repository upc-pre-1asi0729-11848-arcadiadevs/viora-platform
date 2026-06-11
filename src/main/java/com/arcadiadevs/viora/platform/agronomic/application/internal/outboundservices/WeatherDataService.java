package com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherSnapshot;

import java.util.Optional;

/**
 * Outbound service interface for retrieving weather data.
 *
 * <p>
 * Weather is location-based, so the lookup is scoped to a specific plot whose
 * geographic boundary determines the coordinates queried against the provider.
 * </p>
 */
public interface WeatherDataService {

    /**
     * Retrieves the current weather snapshot for the location of a plot.
     *
     * @param plot The plot whose location is used to resolve the weather.
     * @return An Optional containing the WeatherSnapshot if available, otherwise empty.
     */
    Optional<WeatherSnapshot> getCurrentWeatherSnapshot(Plot plot);
}
