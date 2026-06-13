package com.arcadiadevs.viora.platform.agronomic.application.commandservices;

import com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices.AgroMonitoringImageryService;
import com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices.WeatherDataService;
import com.arcadiadevs.viora.platform.agronomic.application.readmodels.AgronomicStatisticsIngestionReport;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.AgronomicStatistic;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.IngestAgronomicStatisticsCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.ChillAccumulationCalculator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AccumulatedChillHours;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillModelState;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillPortions;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DateRange;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MeasurementDate;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviValue;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.SatelliteImagery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.AgronomicStatisticRepository;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.PlotRepository;
import com.arcadiadevs.viora.platform.shared.application.result.ApplicationError;
import com.arcadiadevs.viora.platform.shared.application.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Command service that ingests real agronomic statistic snapshots.
 *
 * <p>
 * For each active plot it persists at most one snapshot per day, combining the
 * real satellite NDVI with chill accumulated from real hourly weather. A plot is
 * skipped when it already has today's snapshot (idempotency) or when no real
 * NDVI is available, so the pipeline never fabricates vegetation data.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AgronomicStatisticIngestionService {

    private final PlotRepository plotRepository;
    private final AgronomicStatisticRepository agronomicStatisticRepository;
    private final AgroMonitoringImageryService agroMonitoringImageryService;
    private final WeatherDataService weatherDataService;
    private final ChillAccumulationCalculator chillAccumulationCalculator;

    /**
     * Ingests today's snapshots for the active plots of a user (on demand).
     *
     * @param command Command carrying the owner identifier.
     * @return Report with the number of ingested and skipped plots.
     */
    @Transactional
    public Result<AgronomicStatisticsIngestionReport, ApplicationError> handle(
            IngestAgronomicStatisticsCommand command
    ) {
        try {
            var userId = new UserId(command.userId());
            var plots = plotRepository.findByUserId(userId);
            return Result.success(ingest(plots, LocalDate.now()));
        } catch (IllegalArgumentException exception) {
            return Result.failure(ApplicationError.validationError(
                    "ingest-agronomic-statistics",
                    exception.getMessage()
            ));
        }
    }

    /**
     * Ingests today's snapshots for every active plot (scheduled job entry point).
     *
     * @return Report with the number of ingested and skipped plots.
     */
    @Transactional
    public AgronomicStatisticsIngestionReport ingestAllActivePlots() {
        return ingest(plotRepository.findAll(), LocalDate.now());
    }

    /**
     * Ingests today's snapshot for a single plot. Used to seed monitoring data
     * right after a plot is registered, so the dashboard summary can populate as
     * soon as real NDVI is available instead of waiting for the scheduled job.
     *
     * <p>Best-effort and idempotent: it skips silently when the plot is inactive
     * or when no real NDVI is available yet, never fabricating vegetation data.</p>
     *
     * @param plot the plot to ingest a snapshot for.
     * @return true if a snapshot was persisted, false when skipped.
     */
    @Transactional
    public boolean ingestForPlot(Plot plot) {
        if (plot == null || plot.getId() == null || !plot.isActive()) {
            return false;
        }
        return ingestPlot(plot, LocalDate.now());
    }

    private AgronomicStatisticsIngestionReport ingest(List<Plot> plots, LocalDate today) {
        var report = AgronomicStatisticsIngestionReport.empty();
        for (var plot : plots) {
            if (!plot.isActive()) {
                continue;
            }
            report = ingestPlot(plot, today)
                    ? report.withIngested()
                    : report.withSkipped();
        }
        return report;
    }

    private boolean ingestPlot(Plot plot, LocalDate today) {
        var plotId = plot.getId();
        var measurementDate = new MeasurementDate(today);

        if (agronomicStatisticRepository.findByPlotIdAndMeasurementDate(plotId, measurementDate).isPresent()) {
            return false;
        }

        var ndviMean = agroMonitoringImageryService.findCurrentImagery(plot)
                .map(SatelliteImagery::ndviMean)
                .filter(Objects::nonNull)
                .orElse(null);
        if (ndviMean == null) {
            log.debug("Skipping agronomic snapshot for plot {}: no real NDVI available.", plotId.getValue());
            return false;
        }

        var base = agronomicStatisticRepository.findLatestByPlotId(plotId);
        var incomingState = base.map(AgronomicStatistic::getChillModelState).orElse(ChillModelState.empty());

        var dailyChill = weatherDataService.getWeatherHistory(plot, new DateRange(today, today))
                .map(history -> chillAccumulationCalculator.accumulate(history, incomingState))
                .orElse(new ChillAccumulationCalculator.ChillAccumulation(0.0, 0.0, incomingState));

        double accumulatedChillHours = Math.max(0.0,
                base.map(snapshot -> snapshot.getChillHours().getValue()).orElse(0.0) + dailyChill.chillHours());
        double accumulatedChillPortions = Math.max(0.0,
                base.map(snapshot -> snapshot.getChillPortions().getValue()).orElse(0.0) + dailyChill.chillPortions());

        agronomicStatisticRepository.save(new AgronomicStatistic(
                plot.getUserId(),
                plotId,
                measurementDate,
                new NdviValue(ndviMean),
                new ChillPortions(accumulatedChillPortions),
                new AccumulatedChillHours(accumulatedChillHours),
                dailyChill.newState()
        ));
        return true;
    }
}
