package com.arcadiadevs.viora.platform.agronomic.application.queryservices;

import com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices.WeatherDataService;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.AgronomicStatistic;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetCurrentMonitoringSummaryQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.ChillRequirementResolver;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.ClimateRiskEvaluator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.MitigationRecommendationGenerator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.PlotHealthEvaluator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.YieldForecastEstimator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AccumulatedChillHours;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AreaSize;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillPortions;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillRequirementPolicy;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillRequirementSource;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ClimateRiskLevel;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DynamicNutritionPolicy;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MeasurementDate;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviValue;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotName;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PolygonCoordinates;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherSnapshot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.YieldEstimationPolicy;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.AgronomicStatisticRepository;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.PlotRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MonitoringSummaryQueryServiceTest {

    @Test
    void sumsYieldUsingEachPlotsAreaAndChillRequirement() {
        var userId = new UserId(10L);
        var firstPlot = plot(1L, userId, "10.0");
        var secondPlot = plot(2L, userId, "20.0")
                .configureChillRequirement(
                        new ChillPortions(100.0),
                        ChillRequirementSource.USER_DECLARED
                );

        var plotRepository = mock(PlotRepository.class);
        var statisticRepository = mock(AgronomicStatisticRepository.class);
        var weatherDataService = mock(WeatherDataService.class);

        when(plotRepository.findByUserId(userId)).thenReturn(List.of(firstPlot, secondPlot));
        when(statisticRepository.findAllByUserIdAndMeasurementDateBetween(any(), any()))
                .thenReturn(List.of(
                        statistic(userId, 1L, 0.80, 40.0),
                        statistic(userId, 2L, 0.80, 40.0)
                ));
        when(weatherDataService.getCurrentWeatherSnapshot(firstPlot))
                .thenReturn(Optional.of(new WeatherSnapshot(
                        WeatherStatus.SUNNY,
                        new MeasurementDate(LocalDate.of(2026, 6, 12)),
                        ClimateRiskLevel.LOW,
                        22.0
                )));

        var service = new MonitoringSummaryQueryService(
                plotRepository,
                statisticRepository,
                weatherDataService,
                new ClimateRiskEvaluator(),
                new MitigationRecommendationGenerator(),
                new YieldForecastEstimator(new YieldEstimationPolicy(4.0, 0.20, 0.80, 0.60)),
                new ChillRequirementResolver(new ChillRequirementPolicy(50.0, Map.of("olive", 40.0))),
                new PlotHealthEvaluator(),
                riskPolicy()
        );

        var summary = service.handle(new GetCurrentMonitoringSummaryQuery(userId)).orElseThrow();

        // Plot 1: 40.0 t (40/40 CP). Plot 2: 60.8 t (40/100 CP).
        assertEquals(100.8, summary.getYieldForecast().getValue(), 1e-6);
        assertEquals(ClimateRiskLevel.LOW, summary.getClimateRiskLevel());
    }

    private Plot plot(long id, UserId userId, String areaHectares) {
        var firstPoint = new GeoPoint(-12.0, -77.0);
        return new Plot(
                userId,
                new PlotName("Plot " + id),
                new PolygonCoordinates(List.of(
                        firstPoint,
                        new GeoPoint(-12.0, -76.9),
                        new GeoPoint(-12.1, -76.9),
                        firstPoint
                )),
                new AreaSize(new BigDecimal(areaHectares)),
                "Olive",
                "Sevillana"
        ).restoreIdentity(new PlotId(id));
    }

    private AgronomicStatistic statistic(UserId userId, long plotId, double ndvi, double chillPortions) {
        return new AgronomicStatistic(
                userId,
                new PlotId(plotId),
                new MeasurementDate(LocalDate.of(2026, 6, 11)),
                new NdviValue(ndvi),
                new ChillPortions(chillPortions),
                new AccumulatedChillHours(50.0)
        );
    }

    private DynamicNutritionPolicy riskPolicy() {
        return new DynamicNutritionPolicy(20.0, 0.30, 0.50, 3, 2, 2.5, 3.0, 1.2);
    }
}
