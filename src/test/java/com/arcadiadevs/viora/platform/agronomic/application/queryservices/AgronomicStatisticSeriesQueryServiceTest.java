package com.arcadiadevs.viora.platform.agronomic.application.queryservices;

import com.arcadiadevs.viora.platform.agronomic.application.readmodels.TrendDirection;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.AgronomicStatistic;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetAgronomicStatisticSeriesQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AccumulatedChillHours;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AreaSize;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillPortions;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MeasurementDate;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviValue;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotName;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PolygonCoordinates;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.TimeRange;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.AgronomicStatisticRepository;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.PlotRepository;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.statistics.AgronomicStatisticsProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AgronomicStatisticSeriesQueryServiceTest {

    private static final long OWNER_ID = 10L;
    private static final long PLOT_ID = 1L;

    private AgronomicStatisticRepository statisticRepository;
    private PlotRepository plotRepository;
    private AgronomicStatisticSeriesQueryService service;

    @BeforeEach
    void setUp() {
        statisticRepository = mock(AgronomicStatisticRepository.class);
        plotRepository = mock(PlotRepository.class);
        service = new AgronomicStatisticSeriesQueryService(
                statisticRepository,
                plotRepository,
                new AgronomicStatisticsProperties()
        );
    }

    @Test
    void buildsCurrentWindowSeriesAndPeriodOverPeriodTrend() {
        var today = LocalDate.now();
        when(plotRepository.findById(any())).thenReturn(Optional.of(createPlot()));
        when(statisticRepository.findAllByUserIdAndPlotIdAndMeasurementDateBetween(any(), any(), any()))
                .thenReturn(List.of(
                        statistic(today.minusDays(8), 0.40, 90.0, 200.0),  // previous window
                        statistic(today.minusDays(1), 0.60, 110.0, 250.0), // current window
                        statistic(today, 0.62, 112.0, 252.0)               // current window
                ));

        var series = service.handle(new GetAgronomicStatisticSeriesQuery(
                OWNER_ID, OWNER_ID, PLOT_ID, TimeRange.LAST_7_DAYS)).success().orElseThrow();

        assertEquals(2, series.points().size());
        assertEquals(TrendDirection.UP, series.ndviTrend().direction());
        assertEquals(0.61, series.ndviTrend().currentValue(), 1e-6);
        assertEquals(0.40, series.ndviTrend().previousValue(), 1e-6);
        assertEquals(0.21, series.ndviTrend().change(), 1e-6);
        assertEquals(600.0, series.chillPortionsThreshold(), 1e-6);
    }

    @Test
    void reportsStableTrendWhenPreviousPeriodHasNoData() {
        var today = LocalDate.now();
        when(plotRepository.findById(any())).thenReturn(Optional.of(createPlot()));
        when(statisticRepository.findAllByUserIdAndPlotIdAndMeasurementDateBetween(any(), any(), any()))
                .thenReturn(List.of(statistic(today, 0.62, 112.0, 252.0)));

        var series = service.handle(new GetAgronomicStatisticSeriesQuery(
                OWNER_ID, OWNER_ID, PLOT_ID, TimeRange.LAST_7_DAYS)).success().orElseThrow();

        assertEquals(1, series.points().size());
        assertEquals(TrendDirection.STABLE, series.ndviTrend().direction());
        assertEquals(0.62, series.ndviTrend().currentValue(), 1e-6);
        assertEquals(null, series.ndviTrend().previousValue());
    }

    @Test
    void returnsForbiddenWhenAuthenticatedUserMismatches() {
        var result = service.handle(new GetAgronomicStatisticSeriesQuery(
                OWNER_ID, 999L, null, TimeRange.LAST_7_DAYS));

        assertTrue(result.isFailure());
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
}
