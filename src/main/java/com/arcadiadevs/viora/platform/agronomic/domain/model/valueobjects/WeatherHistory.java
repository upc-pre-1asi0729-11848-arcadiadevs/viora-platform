package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import java.util.Comparator;
import java.util.List;

/**
 * Historical weather observations for a plot location over a requested range.
 *
 * @param readings Past readings ordered by timestamp; never empty.
 */
public record WeatherHistory(
        List<WeatherReading> readings
) {
    public WeatherHistory {
        if (readings == null || readings.isEmpty()) {
            throw new IllegalArgumentException("Weather history must contain at least one reading.");
        }
        readings = List.copyOf(readings).stream()
                .sorted(Comparator.comparing(WeatherReading::timestamp))
                .toList();
    }
}
