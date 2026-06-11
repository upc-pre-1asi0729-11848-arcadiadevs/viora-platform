package com.arcadiadevs.viora.platform.agronomic.infrastructure.external;

import com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices.WeatherDataService;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherSnapshot;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Fallback implementation of {@link WeatherDataService}.
 *
 * <p>
 * Represents unavailable weather when no external provider can supply data.
 * It deliberately returns an empty result instead of manufacturing a neutral
 * temperature or risk level.
 * </p>
 */
@Service
public class DefaultWeatherDataService implements WeatherDataService {

    @Override
    public Optional<WeatherSnapshot> getCurrentWeatherSnapshot(Plot plot) {
        return Optional.empty();
    }
}
