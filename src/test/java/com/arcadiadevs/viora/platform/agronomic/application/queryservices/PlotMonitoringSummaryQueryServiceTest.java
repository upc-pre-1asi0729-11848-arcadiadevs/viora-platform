package com.arcadiadevs.viora.platform.agronomic.application.queryservices;

import com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices.AgroMonitoringImageryService;
import com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices.WeatherDataService;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.AgronomicStatistic;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotMonitoringSummaryQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.ClimateRiskEvaluator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.MitigationRecommendationGenerator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.NdviTrendAnalyzer;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.ChillSeasonEvaluator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.PhenologicalRiskEvaluator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.PlotHealthEvaluator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.ChillRequirementResolver;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.YieldForecastEstimator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.YieldEstimationPolicy;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillRequirementPolicy;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillRequirementSource;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AccumulatedChillHours;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AreaSize;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillPortions;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ClimateRiskLevel;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DataSourceMetadata;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DynamicNutritionPolicy;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeneralHealthStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MeasurementDate;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviHistory;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviStatistic;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviTrendDirection;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviValue;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotName;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PolygonCoordinates;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ProviderDataAvailability;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.SatelliteImagery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherSnapshot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.AgronomicStatisticRepository;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.PlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlotMonitoringSummaryQueryServiceTest {

    private static final long OWNER_ID = 10L;
    private static final long PLOT_ID = 1L;

    private PlotRepository plotRepository;
    private AgronomicStatisticRepository statisticRepository;
    private AgroMonitoringImageryService imageryService;
    private WeatherDataService weatherDataService;
    private ChillSeasonEvaluator chillSeasonEvaluator;
    private PlotMonitoringSummaryQueryService service;

    @BeforeEach
    void setUp() {
        plotRepository = mock(PlotRepository.class);
        statisticRepository = mock(AgronomicStatisticRepository.class);
        imageryService = mock(AgroMonitoringImageryService.class);
        weatherDataService = mock(WeatherDataService.class);
        chillSeasonEvaluator = mock(ChillSeasonEvaluator.class);

        service = new PlotMonitoringSummaryQueryService(
                plotRepository,
                statisticRepository,
                imageryService,
                weatherDataService,
                new NdviTrendAnalyzer(),
                new PlotHealthEvaluator(),
                new PhenologicalRiskEvaluator(),
                chillSeasonEvaluator,
                new ClimateRiskEvaluator(),
                new MitigationRecommendationGenerator(),
                new YieldForecastEstimator(new YieldEstimationPolicy(4.0, 0.20, 0.80, 0.60)),
                new ChillRequirementResolver(new ChillRequirementPolicy(50.0, Map.of("olive", 40.0))),
                riskPolicy()
        );
    }

    @Test
    void consolidatesRealSatelliteNdviTrendWeatherAndFreshness() {
        var plot = createPlot();
        when(plotRepository.findById(any())).thenReturn(Optional.of(plot));
        when(imageryService.findCurrentImagery(any())).thenReturn(Optional.of(new SatelliteImagery(
                "img-1",
                "https://tiles/{z}/{x}/{y}",
                Instant.parse("2026-06-10T00:00:00Z"),
                0.62,
                4.0
        )));
        when(imageryService.findNdviHistory(any(), any())).thenReturn(Optional.of(new NdviHistory(List.of(
                new NdviStatistic(Instant.parse("2026-04-01T00:00:00Z"), 0.40, null, null, null, null, null, null),
                new NdviStatistic(Instant.parse("2026-06-01T00:00:00Z"), 0.62, null, null, null, null, null, null)
        ))));
        when(weatherDataService.getCurrentWeatherSnapshot(any())).thenReturn(Optional.of(new WeatherSnapshot(
                WeatherStatus.SUNNY,
                new MeasurementDate(LocalDate.of(2026, 6, 11)),
                ClimateRiskLevel.LOW,
                24.0
        )));
        when(statisticRepository.findAllByUserIdAndPlotIdAndMeasurementDateBetween(any(), any(), any()))
                .thenReturn(List.of(statistic(0.50, 45.0, 50.0)));
        when(weatherDataService.describeSource(any())).thenReturn(new DataSourceMetadata(
                "AgroMonitoring", ProviderDataAvailability.AVAILABLE, null, 60));
        when(imageryService.describeNdviSource(any())).thenReturn(new DataSourceMetadata(
                "AgroMonitoring", ProviderDataAvailability.AVAILABLE, Instant.parse("2026-06-10T00:00:00Z"), 60));

        var summary = service.handle(new GetPlotMonitoringSummaryQuery(OWNER_ID, PLOT_ID)).success().orElseThrow();

        assertSame(plot, summary.plot());
        assertEquals(0.62, summary.currentNdvi());
        assertEquals(NdviTrendDirection.RISING, summary.ndviTrend().direction());
        assertEquals(45.0, summary.chillPortions());
        // A single reading has no baseline a week back, so the weekly delta is absent.
        assertNull(summary.chillPortionsWeeklyDelta());
        assertEquals(40.0, summary.chillRequirement().value(), 1e-6);
        assertEquals(ChillRequirementSource.SYSTEM_DEFAULT, summary.chillRequirement().source());
        assertEquals(GeneralHealthStatus.HEALTHY, summary.healthStatus());
        assertEquals(ClimateRiskLevel.MODERATE, summary.phenologicalRisk());
        // vigor 0.7 × chill modifier 1.0 × base 4.0 t/ha × 12.5 ha = 35.0 t.
        assertEquals(35.0, summary.yieldForecastTonnes(), 1e-6);
        assertEquals(ClimateRiskLevel.LOW, summary.climateRiskLevel());
        assertFalse(summary.recommendations().isEmpty());
        assertEquals(ProviderDataAvailability.AVAILABLE, summary.climateSource().availability());
        assertEquals(ProviderDataAvailability.AVAILABLE, summary.ndviSource().availability());
        // Latest signal is the weather observation date (2026-06-11).
        assertEquals(Instant.parse("2026-06-11T00:00:00Z"), summary.lastUpdatedAt());
    }

    @Test
    void degradesGracefullyWhenEveryExternalSourceIsUnavailable() {
        var plot = createPlot();
        when(plotRepository.findById(any())).thenReturn(Optional.of(plot));
        when(imageryService.findCurrentImagery(any())).thenReturn(Optional.empty());
        when(imageryService.findNdviHistory(any(), any())).thenReturn(Optional.empty());
        when(weatherDataService.getCurrentWeatherSnapshot(any())).thenReturn(Optional.empty());
        when(statisticRepository.findAllByUserIdAndPlotIdAndMeasurementDateBetween(any(), any(), any()))
                .thenReturn(List.of());
        when(weatherDataService.describeSource(any()))
                .thenReturn(DataSourceMetadata.notConfigured("AgroMonitoring"));
        when(imageryService.describeNdviSource(any()))
                .thenReturn(DataSourceMetadata.notConfigured("AgroMonitoring"));

        var summary = service.handle(new GetPlotMonitoringSummaryQuery(OWNER_ID, PLOT_ID)).success().orElseThrow();

        assertNull(summary.currentNdvi());
        assertNull(summary.ndviTrend());
        assertNull(summary.chillPortions());
        assertEquals(GeneralHealthStatus.UNKNOWN, summary.healthStatus());
        assertEquals(ClimateRiskLevel.UNKNOWN, summary.phenologicalRisk());
        assertNull(summary.yieldForecastTonnes());
        assertNull(summary.weather());
        assertNull(summary.climateRiskLevel());
        assertNull(summary.lastUpdatedAt());
        assertTrue(summary.recommendations().isEmpty());
        assertEquals(ProviderDataAvailability.NOT_CONFIGURED, summary.climateSource().availability());
    }

    @Test
    void fallsBackToPersistedNdviWhenSatelliteHasNoMean() {
        var plot = createPlot();
        when(chillSeasonEvaluator.isInChillRiskWindow(anyDouble(), any())).thenReturn(true);
        when(plotRepository.findById(any())).thenReturn(Optional.of(plot));
        when(imageryService.findCurrentImagery(any())).thenReturn(Optional.of(new SatelliteImagery(
                "img-1", "https://tiles/{z}/{x}/{y}", Instant.parse("2026-06-10T00:00:00Z"), null, 4.0)));
        when(imageryService.findNdviHistory(any(), any())).thenReturn(Optional.empty());
        when(weatherDataService.getCurrentWeatherSnapshot(any())).thenReturn(Optional.empty());
        when(statisticRepository.findAllByUserIdAndPlotIdAndMeasurementDateBetween(any(), any(), any()))
                .thenReturn(List.of(statistic(0.25, 10.0, 50.0)));
        when(weatherDataService.describeSource(any()))
                .thenReturn(DataSourceMetadata.notConfigured("AgroMonitoring"));
        when(imageryService.describeNdviSource(any()))
                .thenReturn(DataSourceMetadata.notConfigured("AgroMonitoring"));

        var summary = service.handle(new GetPlotMonitoringSummaryQuery(OWNER_ID, PLOT_ID)).success().orElseThrow();

        assertEquals(0.25, summary.currentNdvi());
        assertEquals(GeneralHealthStatus.CRITICAL, summary.healthStatus());
        assertEquals(ClimateRiskLevel.HIGH, summary.phenologicalRisk());
    }

    @Test
    void computesChillPortionsWeeklyDeltaFromTheReadingAroundAWeekEarlier() {
        var plot = createPlot();
        when(plotRepository.findById(any())).thenReturn(Optional.of(plot));
        when(imageryService.findCurrentImagery(any())).thenReturn(Optional.empty());
        when(imageryService.findNdviHistory(any(), any())).thenReturn(Optional.empty());
        when(weatherDataService.getCurrentWeatherSnapshot(any())).thenReturn(Optional.empty());
        // Monotonic accumulation: 66 CP a week back, 72 CP now → +6 CP this week.
        // The intermediate 06-05 reading is after the cut-off and must be ignored.
        when(statisticRepository.findAllByUserIdAndPlotIdAndMeasurementDateBetween(any(), any(), any()))
                .thenReturn(List.of(
                        statistic(LocalDate.of(2026, 6, 2), 0.50, 66.0, 50.0),
                        statistic(LocalDate.of(2026, 6, 5), 0.51, 70.0, 52.0),
                        statistic(LocalDate.of(2026, 6, 9), 0.52, 72.0, 54.0)
                ));
        when(weatherDataService.describeSource(any()))
                .thenReturn(DataSourceMetadata.notConfigured("AgroMonitoring"));
        when(imageryService.describeNdviSource(any()))
                .thenReturn(DataSourceMetadata.notConfigured("AgroMonitoring"));

        var summary = service.handle(new GetPlotMonitoringSummaryQuery(OWNER_ID, PLOT_ID)).success().orElseThrow();

        assertEquals(72.0, summary.chillPortions());
        assertEquals(6.0, summary.chillPortionsWeeklyDelta(), 1e-6);
    }

    @Test
    void returnsForbiddenWhenUserDoesNotOwnPlot() {
        when(plotRepository.findById(any())).thenReturn(Optional.of(createPlot()));

        var result = service.handle(new GetPlotMonitoringSummaryQuery(999L, PLOT_ID));

        assertTrue(result.isFailure());
    }

    @Test
    void returnsNotFoundWhenPlotIsMissing() {
        when(plotRepository.findById(any())).thenReturn(Optional.empty());

        var result = service.handle(new GetPlotMonitoringSummaryQuery(OWNER_ID, PLOT_ID));

        assertTrue(result.isFailure());
    }

    private AgronomicStatistic statistic(double ndvi, double chillPortions, double chillHours) {
        return statistic(LocalDate.of(2026, 6, 9), ndvi, chillPortions, chillHours);
    }

    private AgronomicStatistic statistic(LocalDate date, double ndvi, double chillPortions, double chillHours) {
        return new AgronomicStatistic(
                new UserId(OWNER_ID),
                new PlotId(PLOT_ID),
                new MeasurementDate(date),
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

    private DynamicNutritionPolicy riskPolicy() {
        return new DynamicNutritionPolicy(20.0, 0.30, 0.50, 3, 2, 2.5, 3.0, 1.2);
    }
}
