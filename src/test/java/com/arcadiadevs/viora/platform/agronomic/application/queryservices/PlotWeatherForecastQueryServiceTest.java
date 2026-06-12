package com.arcadiadevs.viora.platform.agronomic.application.queryservices;

import com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices.WeatherDataService;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotWeatherForecastQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.WeatherForecastAdvisor;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AreaSize;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DataSourceMetadata;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotName;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PolygonCoordinates;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ProviderDataAvailability;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherForecast;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherHistory;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherReading;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.PlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlotWeatherForecastQueryServiceTest {

    private static final long OWNER_ID = 10L;
    private static final long PLOT_ID = 1L;

    private PlotRepository plotRepository;
    private WeatherDataService weatherDataService;
    private PlotWeatherForecastQueryService service;

    @BeforeEach
    void setUp() {
        plotRepository = mock(PlotRepository.class);
        weatherDataService = mock(WeatherDataService.class);
        service = new PlotWeatherForecastQueryService(
                plotRepository,
                weatherDataService,
                new WeatherForecastAdvisor()
        );
    }

    @Test
    void buildsForecastWithDailyAggregatesWarningsAndAnomaly() {
        when(plotRepository.findById(any())).thenReturn(Optional.of(createPlot()));
        when(weatherDataService.getForecast(any())).thenReturn(Optional.of(new WeatherForecast(
                Instant.parse("2026-06-11T00:00:00Z"),
                List.of(
                        reading("2026-06-11T00:00:00Z", 1.0, WeatherStatus.SUNNY, 0.5, 5.0),
                        reading("2026-06-11T12:00:00Z", 4.0, WeatherStatus.SUNNY, 3.0, 6.0)
                ))));
        when(weatherDataService.getWeatherHistory(any(), any())).thenReturn(Optional.of(new WeatherHistory(
                List.of(reading("2026-06-04T00:00:00Z", 10.0, WeatherStatus.SUNNY, null, null)))));
        when(weatherDataService.describeSource(any())).thenReturn(new DataSourceMetadata(
                "AgroMonitoring", ProviderDataAvailability.AVAILABLE, null, 60));

        var forecast = service.handle(new GetPlotWeatherForecastQuery(OWNER_ID, PLOT_ID)).success().orElseThrow();

        assertEquals(1, forecast.daily().size());
        assertFalse(forecast.warnings().isEmpty()); // frost (min 0.5 °C)
        assertEquals(ProviderDataAvailability.AVAILABLE, forecast.source().availability());
        assertEquals(2, forecast.hourly().size());
    }

    @Test
    void degradesToEmptyForecastWhenProviderHasNoData() {
        when(plotRepository.findById(any())).thenReturn(Optional.of(createPlot()));
        when(weatherDataService.getForecast(any())).thenReturn(Optional.empty());
        when(weatherDataService.describeSource(any()))
                .thenReturn(DataSourceMetadata.notConfigured("AgroMonitoring"));

        var forecast = service.handle(new GetPlotWeatherForecastQuery(OWNER_ID, PLOT_ID)).success().orElseThrow();

        assertTrue(forecast.daily().isEmpty());
        assertTrue(forecast.hourly().isEmpty());
        assertNull(forecast.overallRisk());
        assertNull(forecast.thermalAnomalyCelsius());
        assertEquals(ProviderDataAvailability.NOT_CONFIGURED, forecast.source().availability());
    }

    @Test
    void returnsForbiddenWhenUserDoesNotOwnPlot() {
        when(plotRepository.findById(any())).thenReturn(Optional.of(createPlot()));

        var result = service.handle(new GetPlotWeatherForecastQuery(999L, PLOT_ID));

        assertTrue(result.isFailure());
    }

    private WeatherReading reading(
            String timestamp,
            double temperature,
            WeatherStatus status,
            Double min,
            Double max
    ) {
        return new WeatherReading(
                Instant.parse(timestamp),
                temperature,
                status,
                null,
                null,
                min,
                max,
                null,
                null
        );
    }

    private Plot createPlot() {
        var firstPoint = new GeoPoint(-12.0, -77.0);
        return new Plot(
                new UserId(OWNER_ID),
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
        ).restoreIdentity(new PlotId(PLOT_ID));
    }
}
