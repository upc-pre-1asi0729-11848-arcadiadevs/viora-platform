package com.arcadiadevs.viora.platform.agronomic.infrastructure.external;

import com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices.WeatherDataService;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ClimateRiskLevel;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MeasurementDate;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherSnapshot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Default implementation of WeatherDataService.
 *
 * <p>
 * This provides a default weather snapshot when no external weather API
 * integration is configured. It returns a neutral weather snapshot to allow
 * the application to function without an external weather service dependency.
 * </p>
 */
@Service
public class DefaultWeatherDataService implements WeatherDataService {

    @Override
    public Optional<WeatherSnapshot> getWeatherSnapshot(UserId userId, MeasurementDate date) {
        // Return a default weather snapshot with neutral values.
        // Replace this with an actual external API call when a weather provider is integrated.
        WeatherSnapshot defaultSnapshot = new WeatherSnapshot(
                WeatherStatus.SUNNY,
                date,
                ClimateRiskLevel.LOW,
                20.0 // Default temperature in Celsius
        );
        return Optional.of(defaultSnapshot);
    }
}
