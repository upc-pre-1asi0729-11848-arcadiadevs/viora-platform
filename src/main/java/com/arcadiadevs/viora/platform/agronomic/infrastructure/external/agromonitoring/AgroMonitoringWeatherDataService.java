package com.arcadiadevs.viora.platform.agronomic.infrastructure.external.agromonitoring;

import com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices.WeatherDataService;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ClimateRiskLevel;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MeasurementDate;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherSnapshot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherStatus;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.external.DefaultWeatherDataService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

/**
 * AgroMonitoring adapter that resolves real weather conditions for a plot.
 *
 * <p>
 * Queries the AgroMonitoring current weather endpoint using the plot's
 * representative coordinates, maps the response into a domain
 * {@link WeatherSnapshot} (temperature in Celsius, mapped weather status and a
 * weather-implied climate risk). When the provider is unavailable, the
 * {@link DefaultWeatherDataService} returns an empty result instead of
 * manufacturing weather data.
 * </p>
 */
@Service
@Primary
@Slf4j
public class AgroMonitoringWeatherDataService implements WeatherDataService {

    private static final double KELVIN_OFFSET = 273.15;
    private static final double HIGH_TEMPERATURE_CELSIUS = 38.0;
    private static final double LOW_TEMPERATURE_CELSIUS = 2.0;

    private final AgroMonitoringProperties properties;
    private final RestClient restClient;
    private final DefaultWeatherDataService fallback;

    public AgroMonitoringWeatherDataService(
            AgroMonitoringProperties properties,
            @Qualifier("agroMonitoringRestClient") RestClient restClient,
            DefaultWeatherDataService fallback
    ) {
        this.properties = properties;
        this.restClient = restClient;
        this.fallback = fallback;
    }

    @Override
    public Optional<WeatherSnapshot> getCurrentWeatherSnapshot(Plot plot) {
        if (!properties.isConfigured()) {
            return fallback.getCurrentWeatherSnapshot(plot);
        }

        try {
            GeoPoint location = plot.getPolygonCoordinates().centroid();

            var response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/agro/1.0/weather")
                            .queryParam("lat", location.getLatitude())
                            .queryParam("lon", location.getLongitude())
                            .queryParam("appid", properties.getApiKey())
                            .build())
                    .retrieve()
                    .body(AgroMonitoringWeatherResponse.class);

            if (response == null
                    || response.dt() == null
                    || response.main() == null
                    || response.main().temp() == null) {
                log.warn("AgroMonitoring returned no usable weather for plot {}.", plot.getId().getValue());
                return fallback.getCurrentWeatherSnapshot(plot);
            }

            double temperatureCelsius = roundToOneDecimal(response.main().temp() - KELVIN_OFFSET);
            WeatherStatus status = mapWeatherStatus(response.weather());
            ClimateRiskLevel weatherRisk = deriveWeatherRisk(status, temperatureCelsius);
            var observationDate = Instant.ofEpochSecond(response.dt())
                    .atZone(ZoneOffset.UTC)
                    .toLocalDate();

            return Optional.of(new WeatherSnapshot(
                    status,
                    new MeasurementDate(observationDate),
                    weatherRisk,
                    temperatureCelsius
            ));
        } catch (RestClientException | IllegalArgumentException exception) {
            log.warn(
                    "Unable to fetch AgroMonitoring weather for plot {} ({}).",
                    plot.getId().getValue(),
                    providerFailureReason(exception)
            );
            return fallback.getCurrentWeatherSnapshot(plot);
        }
    }

    private WeatherStatus mapWeatherStatus(List<AgroMonitoringWeatherCondition> conditions) {
        if (conditions == null || conditions.isEmpty() || conditions.getFirst() == null) {
            return WeatherStatus.UNKNOWN;
        }

        var condition = conditions.getFirst().main();
        if (condition == null) {
            return WeatherStatus.UNKNOWN;
        }

        return switch (condition) {
            case "Clear" -> WeatherStatus.SUNNY;
            case "Clouds" -> WeatherStatus.CLOUDY;
            case "Rain", "Drizzle" -> WeatherStatus.RAINY;
            case "Thunderstorm" -> WeatherStatus.STORMY;
            case "Snow" -> WeatherStatus.SNOWY;
            default -> WeatherStatus.UNKNOWN;
        };
    }

    /**
     * Derives the climate risk implied purely by the current weather reading.
     *
     * <p>
     * This is the weather-driven component the {@code ClimateRiskEvaluator} then
     * combines with vegetation (NDVI) and chill signals. It must never label
     * stormy weather as LOW risk, which the {@link WeatherSnapshot} invariant
     * forbids.
     * </p>
     */
    private ClimateRiskLevel deriveWeatherRisk(WeatherStatus status, double temperatureCelsius) {
        if (status == WeatherStatus.STORMY) {
            return ClimateRiskLevel.EXTREME;
        }
        if (status == WeatherStatus.SNOWY
                || temperatureCelsius >= HIGH_TEMPERATURE_CELSIUS
                || temperatureCelsius <= LOW_TEMPERATURE_CELSIUS) {
            return ClimateRiskLevel.HIGH;
        }
        if (status == WeatherStatus.RAINY) {
            return ClimateRiskLevel.MODERATE;
        }
        return ClimateRiskLevel.LOW;
    }

    private double roundToOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private String providerFailureReason(Exception exception) {
        if (exception instanceof RestClientResponseException responseException) {
            return "HTTP " + responseException.getStatusCode().value();
        }
        return exception.getClass().getSimpleName();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record AgroMonitoringWeatherResponse(
            Long dt,
            List<AgroMonitoringWeatherCondition> weather,
            AgroMonitoringMainMetrics main
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record AgroMonitoringWeatherCondition(String main) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record AgroMonitoringMainMetrics(Double temp) {
    }
}
