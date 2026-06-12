package com.arcadiadevs.viora.platform.agronomic.domain.model.services;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherHistory;
import org.springframework.stereotype.Service;

/**
 * Domain service that computes a day's chill accumulation from hourly weather.
 *
 * <p>
 * Chill hours use the classic Chilling Hours model (hours with temperature in
 * the {@code [0, 7.2] °C} range). Chill portions use the Utah Model chill-unit
 * weighting, which credits moderate cold and penalizes warm hours. Each weather
 * reading is treated as one hour, matching the provider's hourly history.
 * </p>
 *
 * <p>
 * These are established, citable models used as a first approximation for the
 * platform's chill metric; the exact weighting should be validated by an
 * agronomist before production agronomic decisions, consistent with the
 * project's stance on agronomic defaults.
 * </p>
 */
@Service
public class ChillAccumulationCalculator {

    static final double CHILLING_HOURS_LOWER_CELSIUS = 0.0;
    static final double CHILLING_HOURS_UPPER_CELSIUS = 7.2;

    /**
     * Computes the chill accumulated over the readings of a single day.
     *
     * @param history The day's hourly weather readings.
     * @return The day's chill hours and chill portions (Utah chill units).
     */
    public DailyChill computeDailyChill(WeatherHistory history) {
        if (history == null) {
            throw new IllegalArgumentException("Weather history is required to compute chill.");
        }

        double chillHours = 0.0;
        double chillPortions = 0.0;
        for (var reading : history.readings()) {
            double temperature = reading.temperatureCelsius();
            if (temperature >= CHILLING_HOURS_LOWER_CELSIUS
                    && temperature <= CHILLING_HOURS_UPPER_CELSIUS) {
                chillHours += 1.0;
            }
            chillPortions += utahChillUnits(temperature);
        }

        return new DailyChill(chillHours, chillPortions);
    }

    /** Utah Model chill units credited to one hour at the given temperature. */
    private double utahChillUnits(double temperatureCelsius) {
        if (temperatureCelsius <= 1.4) {
            return 0.0;
        }
        if (temperatureCelsius <= 2.4) {
            return 0.5;
        }
        if (temperatureCelsius <= 9.1) {
            return 1.0;
        }
        if (temperatureCelsius <= 12.4) {
            return 0.5;
        }
        if (temperatureCelsius <= 15.9) {
            return 0.0;
        }
        if (temperatureCelsius <= 18.0) {
            return -0.5;
        }
        return -1.0;
    }

    /**
     * A single day's chill accumulation.
     *
     * @param chillHours Hours within the chilling range.
     * @param chillPortions Utah-model chill units (may be negative for a warm day).
     */
    public record DailyChill(double chillHours, double chillPortions) {
    }
}
