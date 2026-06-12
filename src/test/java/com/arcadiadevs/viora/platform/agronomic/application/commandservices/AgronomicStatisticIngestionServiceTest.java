package com.arcadiadevs.viora.platform.agronomic.application.commandservices;

import com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices.AgroMonitoringImageryService;
import com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices.WeatherDataService;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.AgronomicStatistic;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.IngestAgronomicStatisticsCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.ChillAccumulationCalculator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AccumulatedChillHours;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AreaSize;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillPortions;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MeasurementDate;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviValue;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotName;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PolygonCoordinates;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.SatelliteImagery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherHistory;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherReading;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.AgronomicStatisticRepository;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.PlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AgronomicStatisticIngestionServiceTest {

    private static final long OWNER_ID = 10L;
    private static final long PLOT_ID = 1L;

    private PlotRepository plotRepository;
    private AgronomicStatisticRepository statisticRepository;
    private AgroMonitoringImageryService imageryService;
    private WeatherDataService weatherDataService;
    private AgronomicStatisticIngestionService service;

    @BeforeEach
    void setUp() {
        plotRepository = mock(PlotRepository.class);
        statisticRepository = mock(AgronomicStatisticRepository.class);
        imageryService = mock(AgroMonitoringImageryService.class);
        weatherDataService = mock(WeatherDataService.class);
        service = new AgronomicStatisticIngestionService(
                plotRepository,
                statisticRepository,
                imageryService,
                weatherDataService,
                new ChillAccumulationCalculator()
        );

        when(plotRepository.findByUserId(any())).thenReturn(List.of(createPlot()));
    }

    @Test
    void persistsDailySnapshotAccumulatingOnThePreviousOne() {
        when(statisticRepository.findByPlotIdAndMeasurementDate(any(), any())).thenReturn(Optional.empty());
        when(imageryService.findCurrentImagery(any())).thenReturn(Optional.of(new SatelliteImagery(
                "img-1", "https://tiles/{z}/{x}/{y}", Instant.parse("2026-06-11T00:00:00Z"), 0.61, 5.0)));
        // Two chilling hours (+1 Utah unit each from the 5°C readings).
        when(weatherDataService.getWeatherHistory(any(), any())).thenReturn(Optional.of(new WeatherHistory(List.of(
                reading(5.0), reading(5.0)))));
        when(statisticRepository.findLatestByPlotId(any()))
                .thenReturn(Optional.of(snapshot(0.50, 100.0, 240.0)));
        when(statisticRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var report = service.handle(new IngestAgronomicStatisticsCommand(OWNER_ID)).success().orElseThrow();

        assertEquals(1, report.ingested());
        var captor = ArgumentCaptor.forClass(AgronomicStatistic.class);
        verify(statisticRepository).save(captor.capture());
        var saved = captor.getValue();
        assertEquals(0.61, saved.getNdviValue().getValue());
        assertEquals(242.0, saved.getChillHours().getValue());     // 240 + 2 chilling hours
        assertEquals(102.0, saved.getChillPortions().getValue());  // 100 + 2 Utah units
    }

    @Test
    void skipsPlotAlreadySnapshottedToday() {
        when(statisticRepository.findByPlotIdAndMeasurementDate(any(), any()))
                .thenReturn(Optional.of(snapshot(0.5, 1.0, 1.0)));

        var report = service.handle(new IngestAgronomicStatisticsCommand(OWNER_ID)).success().orElseThrow();

        assertEquals(0, report.ingested());
        assertEquals(1, report.skipped());
        verify(statisticRepository, never()).save(any());
    }

    @Test
    void skipsPlotWithoutRealNdvi() {
        when(statisticRepository.findByPlotIdAndMeasurementDate(any(), any())).thenReturn(Optional.empty());
        when(imageryService.findCurrentImagery(any())).thenReturn(Optional.empty());

        var report = service.handle(new IngestAgronomicStatisticsCommand(OWNER_ID)).success().orElseThrow();

        assertEquals(0, report.ingested());
        assertEquals(1, report.skipped());
        verify(statisticRepository, never()).save(any());
    }

    private WeatherReading reading(double temperatureCelsius) {
        return new WeatherReading(
                Instant.parse("2026-06-11T00:00:00Z"),
                temperatureCelsius,
                WeatherStatus.UNKNOWN,
                null, null, null, null, null, null
        );
    }

    private AgronomicStatistic snapshot(double ndvi, double chillPortions, double chillHours) {
        return new AgronomicStatistic(
                new UserId(OWNER_ID),
                new PlotId(PLOT_ID),
                new MeasurementDate(java.time.LocalDate.of(2026, 6, 10)),
                new NdviValue(ndvi),
                new ChillPortions(chillPortions),
                new AccumulatedChillHours(chillHours)
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
