package com.arcadiadevs.viora.platform.agronomic.infrastructure.external.agromonitoring;

import com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices.WeatherDataService;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ClimateRiskLevel;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DataSourceMetadata;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DateRange;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MeasurementDate;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ProviderDataAvailability;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherForecast;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherHistory;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherReading;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherSnapshot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherStatus;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.external.DefaultWeatherDataService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

/**
 * AgroMonitoring adapter that resolves real weather conditions for a plot.
 *
 * <p>
 * Queries the AgroMonitoring weather endpoints using the plot's representative
 * coordinates and maps the responses into domain value objects: a current
 * {@link WeatherSnapshot}, a multi-day {@link WeatherForecast} (the provider
 * exposes an hourly horizon of roughly five days) and a {@link WeatherHistory}.
 * Temperatures are converted to Celsius and conditions mapped to a
 * {@link WeatherStatus}. Provider failures, timeouts and exhausted quotas
 * degrade to the {@link DefaultWeatherDataService} (an empty result) instead of
 * manufacturing weather data, and an exhausted quota opens a cooldown window via
 * the {@link AgroMonitoringQuotaGuard}.
 * </p>
 */
@Service
@Primary
@Slf4j
public class AgroMonitoringWeatherDataService implements WeatherDataService {

    private static final String PROVIDER = "AgroMonitoring";
    private static final int HTTP_TOO_MANY_REQUESTS = 429;
    private static final double KELVIN_OFFSET = 273.15;
    private static final double HIGH_TEMPERATURE_CELSIUS = 38.0;
    private static final double LOW_TEMPERATURE_CELSIUS = 2.0;

    private final AgroMonitoringProperties properties;
    private final RestClient restClient;
    private final DefaultWeatherDataService fallback;
    private final AgroMonitoringQuotaGuard quotaGuard;
    private final ExpiringCache<WeatherSnapshot> currentWeatherCache;
    private final ExpiringCache<WeatherForecast> forecastCache;
    private final ExpiringCache<WeatherHistory> weatherHistoryCache;

    public AgroMonitoringWeatherDataService(
            AgroMonitoringProperties properties,
            @Qualifier("agroMonitoringRestClient") RestClient restClient,
            DefaultWeatherDataService fallback,
            AgroMonitoringQuotaGuard quotaGuard
    ) {
        this.properties = properties;
        this.restClient = restClient;
        this.fallback = fallback;
        this.quotaGuard = quotaGuard;
        this.currentWeatherCache = new ExpiringCache<>(
                Duration.ofMinutes(properties.getCurrentWeatherCacheTtlMinutes()));
        this.forecastCache = new ExpiringCache<>(
                Duration.ofMinutes(properties.getForecastCacheTtlMinutes()));
        this.weatherHistoryCache = new ExpiringCache<>(
                Duration.ofMinutes(properties.getWeatherHistoryCacheTtlMinutes()));
    }

    @Override
    public Optional<WeatherSnapshot> getCurrentWeatherSnapshot(Plot plot) {
        if (!properties.isConfigured() || quotaGuard.isQuotaExhausted()) {
            return fallback.getCurrentWeatherSnapshot(plot);
        }

        try {
            GeoPoint location = plot.getPolygonCoordinates().centroid();
            var cacheKey = cacheKey(location);
            var cached = currentWeatherCache.get(cacheKey);
            if (cached.isPresent()) {
                return cached;
            }

            var response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/agro/1.0/weather")
                            .queryParam("lat", location.getLatitude())
                            .queryParam("lon", location.getLongitude())
                            .queryParam("appid", properties.getApiKey())
                            .build())
                    .retrieve()
                    .body(AgroMonitoringWeatherElement.class);

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

            var snapshot = new WeatherSnapshot(
                    status,
                    new MeasurementDate(observationDate),
                    weatherRisk,
                    temperatureCelsius
            );
            currentWeatherCache.put(cacheKey, snapshot);
            return Optional.of(snapshot);
        } catch (RestClientException | IllegalArgumentException exception) {
            handleProviderFailure("weather", plot, exception);
            return fallback.getCurrentWeatherSnapshot(plot);
        }
    }

    @Override
    public Optional<WeatherForecast> getForecast(Plot plot) {
        if (!properties.isConfigured() || quotaGuard.isQuotaExhausted()) {
            return fallback.getForecast(plot);
        }

        try {
            GeoPoint location = plot.getPolygonCoordinates().centroid();
            var cacheKey = cacheKey(location);
            var cached = forecastCache.get(cacheKey);
            if (cached.isPresent()) {
                return cached;
            }

            var response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/agro/1.0/weather/forecast")
                            .queryParam("lat", location.getLatitude())
                            .queryParam("lon", location.getLongitude())
                            .queryParam("appid", properties.getApiKey())
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<AgroMonitoringWeatherElement>>() {
                    });

            var readings = toReadings(response);
            if (readings.isEmpty()) {
                log.warn("AgroMonitoring returned no usable forecast for plot {}.", plot.getId().getValue());
                return fallback.getForecast(plot);
            }

            var forecast = new WeatherForecast(Instant.now(), readings);
            forecastCache.put(cacheKey, forecast);
            return Optional.of(forecast);
        } catch (RestClientException | IllegalArgumentException exception) {
            handleProviderFailure("forecast", plot, exception);
            return fallback.getForecast(plot);
        }
    }

    @Override
    public Optional<WeatherHistory> getWeatherHistory(Plot plot, DateRange range) {
        if (range == null) {
            throw new IllegalArgumentException("Weather history range is required.");
        }
        if (!properties.isConfigured() || quotaGuard.isQuotaExhausted()) {
            return fallback.getWeatherHistory(plot, range);
        }

        try {
            GeoPoint location = plot.getPolygonCoordinates().centroid();
            var cacheKey = historyCacheKey(location, range);
            var cached = weatherHistoryCache.get(cacheKey);
            if (cached.isPresent()) {
                return cached;
            }

            var response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/agro/1.0/weather/history")
                            .queryParam("lat", location.getLatitude())
                            .queryParam("lon", location.getLongitude())
                            .queryParam("type", "hour")
                            .queryParam("start", startEpochSecond(range))
                            .queryParam("end", endEpochSecond(range))
                            .queryParam("appid", properties.getApiKey())
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<AgroMonitoringWeatherElement>>() {
                    });

            var readings = toReadings(response);
            if (readings.isEmpty()) {
                log.warn("AgroMonitoring returned no weather history for plot {}.", plot.getId().getValue());
                return fallback.getWeatherHistory(plot, range);
            }

            var history = new WeatherHistory(readings);
            weatherHistoryCache.put(cacheKey, history);
            return Optional.of(history);
        } catch (RestClientException | IllegalArgumentException exception) {
            handleProviderFailure("weather history", plot, exception);
            return fallback.getWeatherHistory(plot, range);
        }
    }

    @Override
    public DataSourceMetadata describeSource(Plot plot) {
        if (!properties.isConfigured()) {
            return DataSourceMetadata.notConfigured(PROVIDER);
        }

        var availability = quotaGuard.isQuotaExhausted()
                ? ProviderDataAvailability.QUOTA_EXCEEDED
                : ProviderDataAvailability.AVAILABLE;

        return new DataSourceMetadata(
                PROVIDER,
                availability,
                null,
                properties.getWeatherRefreshMinutes()
        );
    }

    private List<WeatherReading> toReadings(List<AgroMonitoringWeatherElement> elements) {
        if (elements == null) {
            return List.of();
        }
        return elements.stream()
                .map(this::toReading)
                .flatMap(Optional::stream)
                .toList();
    }

    private Optional<WeatherReading> toReading(AgroMonitoringWeatherElement element) {
        if (element == null
                || element.dt() == null
                || element.main() == null
                || element.main().temp() == null) {
            return Optional.empty();
        }

        try {
            var wind = element.wind();
            return Optional.of(new WeatherReading(
                    Instant.ofEpochSecond(element.dt()),
                    roundToOneDecimal(element.main().temp() - KELVIN_OFFSET),
                    mapWeatherStatus(element.weather()),
                    element.main().humidity(),
                    element.rain() == null ? null : element.rain().volume(),
                    toCelsius(element.main().tempMin()),
                    toCelsius(element.main().tempMax()),
                    wind == null ? null : wind.speed(),
                    wind == null ? null : wind.gust()
            ));
        } catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }

    private Double toCelsius(Double kelvin) {
        return kelvin == null ? null : roundToOneDecimal(kelvin - KELVIN_OFFSET);
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

    private long startEpochSecond(DateRange range) {
        return range.getStartDate().atStartOfDay(ZoneOffset.UTC).toEpochSecond();
    }

    private long endEpochSecond(DateRange range) {
        return range.getEndDate().plusDays(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond();
    }

    private double roundToOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    /* Rounds coordinates so nearby requests for the same plot share a cache entry. */
    private String cacheKey(GeoPoint location) {
        return String.format(java.util.Locale.ROOT, "%.4f,%.4f",
                location.getLatitude(), location.getLongitude());
    }

    private String historyCacheKey(GeoPoint location, DateRange range) {
        return cacheKey(location) + ":" + range.getStartDate() + ":" + range.getEndDate();
    }

    private void handleProviderFailure(String operation, Plot plot, Exception exception) {
        if (isQuotaRejection(exception)) {
            quotaGuard.recordQuotaExceeded();
        }
        log.warn(
                "Unable to fetch AgroMonitoring {} for plot {} ({}).",
                operation,
                plot.getId().getValue(),
                providerFailureReason(exception)
        );
    }

    private boolean isQuotaRejection(Exception exception) {
        return exception instanceof RestClientResponseException responseException
                && responseException.getStatusCode().value() == HTTP_TOO_MANY_REQUESTS;
    }

    private String providerFailureReason(Exception exception) {
        if (exception instanceof RestClientResponseException responseException) {
            return "HTTP " + responseException.getStatusCode().value();
        }
        return exception.getClass().getSimpleName();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record AgroMonitoringWeatherElement(
            Long dt,
            List<AgroMonitoringWeatherCondition> weather,
            AgroMonitoringMainMetrics main,
            AgroMonitoringPrecipitation rain,
            AgroMonitoringWind wind
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record AgroMonitoringWind(Double speed, Double gust) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record AgroMonitoringWeatherCondition(String main) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record AgroMonitoringMainMetrics(
            Double temp,
            @JsonProperty("temp_min") Double tempMin,
            @JsonProperty("temp_max") Double tempMax,
            Integer humidity
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record AgroMonitoringPrecipitation(
            @JsonProperty("1h") Double oneHour,
            @JsonProperty("3h") Double threeHours
    ) {
        Double volume() {
            return threeHours != null ? threeHours : oneHour;
        }
    }
}
