package com.arcadiadevs.viora.platform.agronomic.infrastructure.external;

import com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices.WeatherDataService;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DataSourceMetadata;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DateRange;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ProviderDataAvailability;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherForecast;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherHistory;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherSnapshot;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Fallback implementation of {@link WeatherDataService}.
 *
 * <p>
 * Represents unavailable weather when no external provider can supply data.
 * It deliberately returns empty results instead of manufacturing neutral
 * temperatures, forecasts or risk levels, and reports the source as
 * {@code UNAVAILABLE}.
 * </p>
 */
@Service
public class DefaultWeatherDataService implements WeatherDataService {

    private static final String PROVIDER = "Unavailable";

    @Override
    public Optional<WeatherSnapshot> getCurrentWeatherSnapshot(Plot plot) {
        return Optional.empty();
    }

    @Override
    public Optional<WeatherForecast> getForecast(Plot plot) {
        return Optional.empty();
    }

    @Override
    public Optional<WeatherHistory> getWeatherHistory(Plot plot, DateRange range) {
        return Optional.empty();
    }

    @Override
    public DataSourceMetadata describeSource(Plot plot) {
        return new DataSourceMetadata(PROVIDER, ProviderDataAvailability.UNAVAILABLE, null, null);
    }
}
