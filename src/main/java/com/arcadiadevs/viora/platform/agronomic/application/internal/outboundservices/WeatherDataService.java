package com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DataSourceMetadata;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DateRange;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherForecast;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherHistory;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherSnapshot;

import java.util.Optional;

/**
 * Outbound service interface for retrieving weather data.
 *
 * <p>
 * Weather is location-based, so every lookup is scoped to a specific plot whose
 * geographic boundary determines the coordinates queried against the provider.
 * Implementations must degrade gracefully: provider failures, timeouts and
 * exhausted quotas return an empty result rather than propagating exceptions,
 * and {@link #describeSource(Plot)} reports why data may be missing.
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

    /**
     * Retrieves the multi-day weather forecast for the location of a plot.
     *
     * <p>The provider exposes an hourly horizon of roughly five days; callers
     * derive daily highs and lows from the returned readings.</p>
     *
     * @param plot The plot whose location is used to resolve the forecast.
     * @return The forecast when available, otherwise empty.
     */
    Optional<WeatherForecast> getForecast(Plot plot);

    /**
     * Retrieves historical weather observations for the location of a plot.
     *
     * @param plot The plot whose location is used to resolve the history.
     * @param range Inclusive day range to query.
     * @return The weather history when available, otherwise empty.
     */
    Optional<WeatherHistory> getWeatherHistory(Plot plot, DateRange range);

    /**
     * Describes the weather source and its freshness for a plot.
     *
     * @param plot The plot whose weather source is described.
     * @return Provider identity, availability and update cadence metadata.
     */
    DataSourceMetadata describeSource(Plot plot);
}
