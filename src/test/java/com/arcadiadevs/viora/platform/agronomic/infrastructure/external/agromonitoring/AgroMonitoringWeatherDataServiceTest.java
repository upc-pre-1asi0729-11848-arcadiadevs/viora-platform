package com.arcadiadevs.viora.platform.agronomic.infrastructure.external.agromonitoring;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AreaSize;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ClimateRiskLevel;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DateRange;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotName;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PolygonCoordinates;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ProviderDataAvailability;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherStatus;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.external.DefaultWeatherDataService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class AgroMonitoringWeatherDataServiceTest {

    @Test
    void mapsCurrentProviderWeatherAndUsesProviderObservationDate() {
        var properties = configuredProperties();
        var builder = RestClient.builder();
        var server = MockRestServiceServer.bindTo(builder).build();
        var service = new AgroMonitoringWeatherDataService(
                properties,
                builder.baseUrl(properties.getBaseUrl()).build(),
                new DefaultWeatherDataService(),
                new AgroMonitoringQuotaGuard(properties)
        );
        long observedAt = Instant.parse("2026-06-10T18:30:00Z").getEpochSecond();

        server.expect(once(), request -> {
                    assertEquals("/agro/1.0/weather", request.getURI().getPath());
                    assertTrue(request.getURI().getQuery().contains("appid=test-key"));
                    assertTrue(request.getURI().getQuery().contains("lat="));
                    assertTrue(request.getURI().getQuery().contains("lon="));
                })
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "dt": %d,
                          "weather": [{"main": "Clouds"}],
                          "main": {"temp": 298.45}
                        }
                        """.formatted(observedAt), MediaType.APPLICATION_JSON));

        var result = service.getCurrentWeatherSnapshot(createPlot()).orElseThrow();

        assertEquals(WeatherStatus.CLOUDY, result.getWeatherStatus());
        assertEquals(ClimateRiskLevel.LOW, result.getClimateRiskLevel());
        assertEquals(25.3, result.getTemperature());
        assertEquals(
                Instant.ofEpochSecond(observedAt).atZone(ZoneOffset.UTC).toLocalDate(),
                result.getMeasurementDate().getValue()
        );
        server.verify();
    }

    @Test
    void returnsEmptyWithoutCallingProviderWhenIntegrationIsDisabled() {
        var properties = new AgroMonitoringProperties();
        var service = new AgroMonitoringWeatherDataService(
                properties,
                RestClient.create(),
                new DefaultWeatherDataService(),
                new AgroMonitoringQuotaGuard(properties)
        );

        assertTrue(service.getCurrentWeatherSnapshot(createPlot()).isEmpty());
        assertTrue(service.getForecast(createPlot()).isEmpty());
        assertTrue(service.getWeatherHistory(createPlot(), lastSevenDays()).isEmpty());
        assertEquals(
                ProviderDataAvailability.NOT_CONFIGURED,
                service.describeSource(createPlot()).availability()
        );
    }

    @Test
    void mapsMultiDayForecastReadingsWithDailyExtremes() {
        var properties = configuredProperties();
        var builder = RestClient.builder();
        var server = MockRestServiceServer.bindTo(builder).build();
        var service = new AgroMonitoringWeatherDataService(
                properties,
                builder.baseUrl(properties.getBaseUrl()).build(),
                new DefaultWeatherDataService(),
                new AgroMonitoringQuotaGuard(properties)
        );

        server.expect(once(), request -> {
                    assertEquals("/agro/1.0/weather/forecast", request.getURI().getPath());
                    assertTrue(request.getURI().getQuery().contains("appid=test-key"));
                })
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        [
                          {
                            "dt": 1771000000,
                            "weather": [{"main": "Clear"}],
                            "main": {"temp": 298.15, "temp_min": 295.15, "temp_max": 301.15, "humidity": 60},
                            "rain": {"3h": 1.2}
                          },
                          {
                            "dt": 1770000000,
                            "weather": [{"main": "Rain"}],
                            "main": {"temp": 290.15, "humidity": 80}
                          }
                        ]
                        """, MediaType.APPLICATION_JSON));

        var forecast = service.getForecast(createPlot()).orElseThrow();

        assertEquals(2, forecast.readings().size());
        // Readings are returned ordered by timestamp, so the earlier dt comes first.
        var earliest = forecast.earliest();
        assertEquals(Instant.ofEpochSecond(1770000000L), earliest.timestamp());
        assertEquals(WeatherStatus.RAINY, earliest.weatherStatus());
        assertEquals(80, earliest.humidityPercentage());

        var latest = forecast.latest();
        assertEquals(25.0, latest.temperatureCelsius());
        assertEquals(22.0, latest.minTemperatureCelsius());
        assertEquals(28.0, latest.maxTemperatureCelsius());
        assertEquals(1.2, latest.precipitationMillimeters());
        server.verify();
    }

    @Test
    void requestsHistoryWindowAndMapsObservations() {
        var properties = configuredProperties();
        var builder = RestClient.builder();
        var server = MockRestServiceServer.bindTo(builder).build();
        var service = new AgroMonitoringWeatherDataService(
                properties,
                builder.baseUrl(properties.getBaseUrl()).build(),
                new DefaultWeatherDataService(),
                new AgroMonitoringQuotaGuard(properties)
        );

        server.expect(once(), request -> {
                    assertEquals("/agro/1.0/weather/history", request.getURI().getPath());
                    var query = request.getURI().getQuery();
                    assertTrue(query.contains("type=hour"));
                    assertTrue(query.contains("start="));
                    assertTrue(query.contains("end="));
                })
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        [
                          {
                            "dt": 1769000000,
                            "weather": [{"main": "Clouds"}],
                            "main": {"temp": 293.15, "humidity": 70},
                            "rain": {"1h": 0.5}
                          }
                        ]
                        """, MediaType.APPLICATION_JSON));

        var history = service.getWeatherHistory(createPlot(), lastSevenDays()).orElseThrow();

        assertEquals(1, history.readings().size());
        var reading = history.readings().getFirst();
        assertEquals(20.0, reading.temperatureCelsius());
        assertEquals(0.5, reading.precipitationMillimeters());
        server.verify();
    }

    @Test
    void pausesProviderCallsAfterQuotaRejection() {
        var properties = configuredProperties();
        var builder = RestClient.builder();
        var server = MockRestServiceServer.bindTo(builder).build();
        var quotaGuard = new AgroMonitoringQuotaGuard(properties);
        var service = new AgroMonitoringWeatherDataService(
                properties,
                builder.baseUrl(properties.getBaseUrl()).build(),
                new DefaultWeatherDataService(),
                quotaGuard
        );

        server.expect(once(), request ->
                        assertEquals("/agro/1.0/weather/forecast", request.getURI().getPath()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));

        // First call hits the provider, receives a 429 and opens the cooldown window.
        assertTrue(service.getForecast(createPlot()).isEmpty());
        assertTrue(quotaGuard.isQuotaExhausted());

        // Subsequent calls short-circuit without touching the provider.
        assertTrue(service.getCurrentWeatherSnapshot(createPlot()).isEmpty());
        assertEquals(
                ProviderDataAvailability.QUOTA_EXCEEDED,
                service.describeSource(createPlot()).availability()
        );
        server.verify();
    }

    @Test
    void cachesForecastToProtectTheProviderAccount() {
        var properties = configuredProperties();
        var builder = RestClient.builder();
        var server = MockRestServiceServer.bindTo(builder).build();
        var service = new AgroMonitoringWeatherDataService(
                properties,
                builder.baseUrl(properties.getBaseUrl()).build(),
                new DefaultWeatherDataService(),
                new AgroMonitoringQuotaGuard(properties)
        );

        server.expect(once(), request ->
                        assertEquals("/agro/1.0/weather/forecast", request.getURI().getPath()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        [ {"dt": 1771000000, "weather": [{"main": "Clear"}], "main": {"temp": 298.15}} ]
                        """, MediaType.APPLICATION_JSON));

        var first = service.getForecast(createPlot());
        var second = service.getForecast(createPlot());

        assertTrue(first.isPresent());
        assertTrue(second.isPresent());
        // Only one provider call despite two forecast requests for the same plot.
        server.verify();
    }

    @Test
    void cachesWeatherHistoryToProtectTheProviderAccount() {
        var properties = configuredProperties();
        var builder = RestClient.builder();
        var server = MockRestServiceServer.bindTo(builder).build();
        var service = new AgroMonitoringWeatherDataService(
                properties,
                builder.baseUrl(properties.getBaseUrl()).build(),
                new DefaultWeatherDataService(),
                new AgroMonitoringQuotaGuard(properties)
        );

        server.expect(once(), request ->
                        assertEquals("/agro/1.0/weather/history", request.getURI().getPath()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        [ {"dt": 1769000000, "weather": [{"main": "Clouds"}], "main": {"temp": 293.15}} ]
                        """, MediaType.APPLICATION_JSON));

        var range = lastSevenDays();
        assertTrue(service.getWeatherHistory(createPlot(), range).isPresent());
        assertTrue(service.getWeatherHistory(createPlot(), range).isPresent());
        server.verify();
    }

    private DateRange lastSevenDays() {
        var today = LocalDate.of(2026, 6, 11);
        return new DateRange(today.minusDays(6), today);
    }

    private AgroMonitoringProperties configuredProperties() {
        var properties = new AgroMonitoringProperties();
        properties.setEnabled(true);
        properties.setBaseUrl("https://api.agromonitoring.com");
        properties.setApiKey("test-key");
        return properties;
    }

    private Plot createPlot() {
        var firstPoint = new GeoPoint(-12.0, -77.0);
        return new Plot(
                new UserId(10L),
                new PlotName("North field"),
                new PolygonCoordinates(List.of(
                        firstPoint,
                        new GeoPoint(-12.0, -76.9),
                        new GeoPoint(-12.1, -76.9),
                        firstPoint
                )),
                new AreaSize(new BigDecimal("12.50")),
                "Olive",
                "Sevillana"
        ).restoreIdentity(new PlotId(1L));
    }
}
