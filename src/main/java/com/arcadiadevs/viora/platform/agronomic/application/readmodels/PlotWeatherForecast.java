package com.arcadiadevs.viora.platform.agronomic.application.readmodels;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AgronomicWeatherWarning;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ClimateRiskLevel;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DailyWeather;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DataSourceMetadata;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherReading;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Per-plot weather forecast projection for the forecast screen.
 *
 * <p>
 * Exposes the provider's hourly readings (about five days), daily aggregates,
 * the thermal anomaly versus a recent baseline, agronomic warnings and overall
 * risk, plus the source freshness. When the provider has no forecast the lists
 * are empty and {@code overallRisk}/{@code generatedAt} are null, so the screen
 * can render a "not available" state.
 * </p>
 *
 * @param plot The plot aggregate.
 * @param generatedAt Instant the forecast was retrieved, or null when unavailable.
 * @param hourly Hourly forecast readings.
 * @param daily Daily aggregates.
 * @param thermalAnomalyCelsius Forecast mean minus recent baseline, or null.
 * @param overallRisk Highest climate risk implied by warnings, or null when unavailable.
 * @param warnings Agronomic weather warnings.
 * @param source Weather source identity, availability and freshness.
 */
public record PlotWeatherForecast(
        Plot plot,
        Instant generatedAt,
        List<WeatherReading> hourly,
        List<DailyWeather> daily,
        Double thermalAnomalyCelsius,
        ClimateRiskLevel overallRisk,
        List<AgronomicWeatherWarning> warnings,
        DataSourceMetadata source
) {
    public PlotWeatherForecast {
        Objects.requireNonNull(plot, "Plot is required.");
        Objects.requireNonNull(source, "Source metadata is required.");
        hourly = hourly == null ? List.of() : List.copyOf(hourly);
        daily = daily == null ? List.of() : List.copyOf(daily);
        warnings = warnings == null ? List.of() : List.copyOf(warnings);
    }
}
