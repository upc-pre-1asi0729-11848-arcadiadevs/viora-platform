package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

/**
 * A multi-day weather forecast for a plot location.
 *
 * <p>
 * Holds the chronologically ordered {@link WeatherReading readings} returned by
 * the provider (AgroMonitoring exposes an hourly horizon of roughly five days).
 * Daily highs and lows are derived by callers from these readings rather than
 * fabricated, since the provider does not offer a longer daily horizon.
 * </p>
 *
 * @param generatedAt Instant the forecast was retrieved from the provider.
 * @param readings Forecast readings ordered by timestamp; never empty.
 */
public record WeatherForecast(
        Instant generatedAt,
        List<WeatherReading> readings
) {
    public WeatherForecast {
        if (generatedAt == null) {
            throw new IllegalArgumentException("Forecast generation instant is required.");
        }
        if (readings == null || readings.isEmpty()) {
            throw new IllegalArgumentException("A forecast must contain at least one reading.");
        }
        readings = List.copyOf(readings).stream()
                .sorted(Comparator.comparing(WeatherReading::timestamp))
                .toList();
    }

    /** Earliest reading in the forecast horizon. */
    public WeatherReading earliest() {
        return readings.getFirst();
    }

    /** Latest reading in the forecast horizon. */
    public WeatherReading latest() {
        return readings.getLast();
    }
}
