package com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MeasurementDate;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherSnapshot;

import java.util.Optional;

/**
 * Outbound service interface for retrieving weather data.
 */
public interface WeatherDataService {

    /**
     * Retrieves the weather snapshot for a specific user and date.
     *
     * @param userId The ID of the user.
     * @param date The measurement date for the weather snapshot.
     * @return An Optional containing the WeatherSnapshot if available, otherwise empty.
     */
    Optional<WeatherSnapshot> getWeatherSnapshot(UserId userId, MeasurementDate date);
}