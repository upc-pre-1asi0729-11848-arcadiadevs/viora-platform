package com.arcadiadevs.viora.platform.agronomic.infrastructure.external.agromonitoring;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AreaSize;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ClimateRiskLevel;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotName;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PolygonCoordinates;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherStatus;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.external.DefaultWeatherDataService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
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
                new DefaultWeatherDataService()
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
                new DefaultWeatherDataService()
        );

        assertTrue(service.getCurrentWeatherSnapshot(createPlot()).isEmpty());
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
