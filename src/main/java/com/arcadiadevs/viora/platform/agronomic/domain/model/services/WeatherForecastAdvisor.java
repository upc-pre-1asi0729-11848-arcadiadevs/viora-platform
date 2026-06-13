package com.arcadiadevs.viora.platform.agronomic.domain.model.services;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AgronomicWeatherWarning;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ClimateRiskLevel;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DailyWeather;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherForecast;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherForecastAnalysis;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherReading;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherWarningType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Domain service that turns a weather forecast into agronomic guidance.
 *
 * <p>
 * Aggregates the hourly readings into daily summaries (min/max/mean temperature,
 * dominant condition, humidity, precipitation and wind gust), estimates the
 * thermal anomaly against a baseline mean temperature, and raises agronomic
 * warnings for frost, heat stress, storms, damaging wind and heavy rain. The
 * overall risk is the highest severity among the warnings.
 * </p>
 *
 * <p>The thresholds are course-level defaults and should be validated by an
 * agronomist for the target crop before production use.</p>
 */
@Service
public class WeatherForecastAdvisor {

    static final double FROST_MINIMUM_CELSIUS = 2.0;
    static final double HEAT_STRESS_MAXIMUM_CELSIUS = 35.0;
    static final double HIGH_WIND_GUST_METERS_PER_SECOND = 17.0;
    static final double HEAVY_RAIN_MILLIMETERS = 20.0;

    /**
     * Analyzes a forecast into daily summaries, a thermal anomaly and warnings.
     *
     * @param forecast The forecast to analyze.
     * @param baselineMeanTemperatureCelsius Recent baseline mean temperature for
     *                                       the anomaly, or null when unknown.
     * @return The agronomic forecast analysis.
     */
    public WeatherForecastAnalysis analyze(WeatherForecast forecast, Double baselineMeanTemperatureCelsius) {
        if (forecast == null) {
            throw new IllegalArgumentException("Forecast is required.");
        }

        var readingsByDay = forecast.readings().stream()
                .collect(Collectors.groupingBy(
                        reading -> reading.timestamp().atZone(ZoneOffset.UTC).toLocalDate(),
                        TreeMap::new,
                        Collectors.toList()
                ));

        var dailyForecasts = new ArrayList<DailyWeather>();
        var warnings = new ArrayList<AgronomicWeatherWarning>();
        readingsByDay.forEach((day, readings) -> {
            var daily = summarize(day, readings);
            dailyForecasts.add(daily);
            warnings.addAll(warningsFor(daily, readings));
        });

        return new WeatherForecastAnalysis(
                dailyForecasts,
                thermalAnomaly(forecast, baselineMeanTemperatureCelsius),
                overallRisk(warnings),
                warnings
        );
    }

    private DailyWeather summarize(LocalDate day, List<WeatherReading> readings) {
        double minimum = readings.stream()
                .mapToDouble(reading -> reading.minTemperatureCelsius() != null
                        ? reading.minTemperatureCelsius()
                        : reading.temperatureCelsius())
                .min()
                .orElseThrow();
        double maximum = readings.stream()
                .mapToDouble(reading -> reading.maxTemperatureCelsius() != null
                        ? reading.maxTemperatureCelsius()
                        : reading.temperatureCelsius())
                .max()
                .orElseThrow();
        double average = readings.stream()
                .mapToDouble(WeatherReading::temperatureCelsius)
                .average()
                .orElseThrow();
        double totalPrecipitation = readings.stream()
                .map(WeatherReading::precipitationMillimeters)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();

        OptionalDouble maxGust = readings.stream()
                .filter(reading -> reading.windGustMetersPerSecond() != null)
                .mapToDouble(WeatherReading::windGustMetersPerSecond)
                .max();

        return new DailyWeather(
                day,
                round1(minimum),
                round1(maximum),
                round1(average),
                dominantStatus(readings),
                averageHumidity(readings),
                round1(totalPrecipitation),
                maxGust.isPresent() ? round1(maxGust.getAsDouble()) : null
        );
    }

    private WeatherStatus dominantStatus(List<WeatherReading> readings) {
        return readings.stream()
                .collect(Collectors.groupingBy(WeatherReading::weatherStatus, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(WeatherStatus.UNKNOWN);
    }

    private Integer averageHumidity(List<WeatherReading> readings) {
        OptionalDouble average = readings.stream()
                .map(WeatherReading::humidityPercentage)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average();
        return average.isPresent() ? (int) Math.round(average.getAsDouble()) : null;
    }

    private List<AgronomicWeatherWarning> warningsFor(DailyWeather daily, List<WeatherReading> readings) {
        var warnings = new ArrayList<AgronomicWeatherWarning>();

        if (daily.minTemperatureCelsius() <= FROST_MINIMUM_CELSIUS) {
            warnings.add(warning(WeatherWarningType.FROST, ClimateRiskLevel.HIGH, daily.date(),
                    "Frost risk: minimum temperature %.1f °C.".formatted(daily.minTemperatureCelsius())));
        }
        if (daily.maxTemperatureCelsius() >= HEAT_STRESS_MAXIMUM_CELSIUS) {
            warnings.add(warning(WeatherWarningType.HEAT_STRESS, ClimateRiskLevel.HIGH, daily.date(),
                    "Heat stress risk: maximum temperature %.1f °C.".formatted(daily.maxTemperatureCelsius())));
        }
        if (readings.stream().anyMatch(reading -> reading.weatherStatus() == WeatherStatus.STORMY)) {
            warnings.add(warning(WeatherWarningType.STORM, ClimateRiskLevel.EXTREME, daily.date(),
                    "Thunderstorm conditions forecast."));
        }
        if (daily.maxWindGustMetersPerSecond() != null
                && daily.maxWindGustMetersPerSecond() >= HIGH_WIND_GUST_METERS_PER_SECOND) {
            warnings.add(warning(WeatherWarningType.HIGH_WIND, ClimateRiskLevel.MODERATE, daily.date(),
                    "Damaging wind gusts up to %.1f m/s.".formatted(daily.maxWindGustMetersPerSecond())));
        }
        if (daily.totalPrecipitationMillimeters() >= HEAVY_RAIN_MILLIMETERS) {
            warnings.add(warning(WeatherWarningType.HEAVY_RAIN, ClimateRiskLevel.MODERATE, daily.date(),
                    "Heavy rain: %.1f mm expected.".formatted(daily.totalPrecipitationMillimeters())));
        }

        return warnings;
    }

    private AgronomicWeatherWarning warning(
            WeatherWarningType type,
            ClimateRiskLevel severity,
            LocalDate date,
            String message
    ) {
        return new AgronomicWeatherWarning(type, severity, date, message);
    }

    private Double thermalAnomaly(WeatherForecast forecast, Double baselineMeanTemperatureCelsius) {
        if (baselineMeanTemperatureCelsius == null) {
            return null;
        }
        double forecastMean = forecast.readings().stream()
                .mapToDouble(WeatherReading::temperatureCelsius)
                .average()
                .orElse(baselineMeanTemperatureCelsius);
        return round1(forecastMean - baselineMeanTemperatureCelsius);
    }

    private ClimateRiskLevel overallRisk(List<AgronomicWeatherWarning> warnings) {
        return warnings.stream()
                .map(AgronomicWeatherWarning::severity)
                .max((first, second) -> Integer.compare(rank(first), rank(second)))
                .orElse(ClimateRiskLevel.LOW);
    }

    private int rank(ClimateRiskLevel level) {
        return switch (level) {
            case LOW -> 1;
            case MODERATE -> 2;
            case HIGH -> 3;
            case EXTREME -> 4;
            default -> 0;
        };
    }

    private double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
