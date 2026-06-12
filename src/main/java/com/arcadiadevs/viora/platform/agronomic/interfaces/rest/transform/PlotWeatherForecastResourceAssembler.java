package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.agronomic.application.readmodels.PlotWeatherForecast;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AgronomicWeatherWarning;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DailyWeather;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DataSourceMetadata;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherReading;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.PlotWeatherForecastResource;

/**
 * Maps the per-plot weather forecast projection to its REST resource.
 */
public final class PlotWeatherForecastResourceAssembler {

    private PlotWeatherForecastResourceAssembler() {
    }

    public static PlotWeatherForecastResource toResourceFromReadModel(PlotWeatherForecast forecast) {
        var plot = forecast.plot();
        return new PlotWeatherForecastResource(
                plot.getId().getValue(),
                plot.getUserId().getValue(),
                plot.getName().getValue(),
                forecast.generatedAt(),
                forecast.hourly().stream()
                        .map(PlotWeatherForecastResourceAssembler::toHourlyResource)
                        .toList(),
                forecast.daily().stream()
                        .map(PlotWeatherForecastResourceAssembler::toDailyResource)
                        .toList(),
                forecast.thermalAnomalyCelsius(),
                forecast.overallRisk() == null ? null : forecast.overallRisk().name(),
                forecast.warnings().stream()
                        .map(PlotWeatherForecastResourceAssembler::toWarningResource)
                        .toList(),
                toDataSourceResource(forecast.source())
        );
    }

    private static PlotWeatherForecastResource.HourlyForecastResource toHourlyResource(WeatherReading reading) {
        return new PlotWeatherForecastResource.HourlyForecastResource(
                reading.timestamp(),
                reading.temperatureCelsius(),
                reading.weatherStatus().name(),
                reading.humidityPercentage(),
                reading.precipitationMillimeters(),
                reading.windSpeedMetersPerSecond(),
                reading.windGustMetersPerSecond()
        );
    }

    private static PlotWeatherForecastResource.DailyForecastResource toDailyResource(DailyWeather daily) {
        return new PlotWeatherForecastResource.DailyForecastResource(
                daily.date(),
                daily.minTemperatureCelsius(),
                daily.maxTemperatureCelsius(),
                daily.averageTemperatureCelsius(),
                daily.dominantStatus().name(),
                daily.averageHumidityPercentage(),
                daily.totalPrecipitationMillimeters(),
                daily.maxWindGustMetersPerSecond()
        );
    }

    private static PlotWeatherForecastResource.WeatherWarningResource toWarningResource(AgronomicWeatherWarning warning) {
        return new PlotWeatherForecastResource.WeatherWarningResource(
                warning.type().name(),
                warning.severity().name(),
                warning.date(),
                warning.message()
        );
    }

    private static PlotWeatherForecastResource.DataSourceResource toDataSourceResource(DataSourceMetadata metadata) {
        return new PlotWeatherForecastResource.DataSourceResource(
                metadata.provider(),
                metadata.availability().name(),
                metadata.lastReadingAt(),
                metadata.updateFrequencyMinutes()
        );
    }
}
