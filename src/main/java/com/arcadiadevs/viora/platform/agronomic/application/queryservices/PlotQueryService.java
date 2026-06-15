package com.arcadiadevs.viora.platform.agronomic.application.queryservices;

import com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices.AgroMonitoringImageryService;
import com.arcadiadevs.viora.platform.agronomic.application.readmodels.IntegrationLinkStatus;
import com.arcadiadevs.viora.platform.agronomic.application.readmodels.MyPlotsOverview;
import com.arcadiadevs.viora.platform.agronomic.application.readmodels.PlotMonitoringOverview;
import com.arcadiadevs.viora.platform.agronomic.application.readmodels.PlotWithCurrentImagery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.AgronomicStatistic;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetMyPlotsOverviewQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotByIdQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotNdviTileQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotsByUserIdQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotsWithCurrentImageryQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.ChillRequirementResolver;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.PhenologicalRiskEvaluator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.PlotHealthEvaluator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DateRange;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.IoTDeviceStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MeasurementDate;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.SatelliteImagery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.AgronomicStatisticRepository;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.IoTDeviceRepository;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.PlotRepository;
import com.arcadiadevs.viora.platform.shared.application.result.ApplicationError;
import com.arcadiadevs.viora.platform.shared.application.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Plot query service.
 *
 * <p>
 *     Application service responsible for handling read operations related to plots.
 *     It coordinates plot queries with the domain repository and returns explicit
 *     Result values instead of throwing application-level exceptions.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class PlotQueryService {

    private static final int OVERVIEW_STATISTICS_LOOKBACK_DAYS = 30;

    /**
     * Plot repository port.
     */
    private final PlotRepository plotRepository;

    /**
     * Satellite imagery outbound service.
     */
    private final AgroMonitoringImageryService agroMonitoringImageryService;

    /**
     * Agronomic statistic repository port.
     */
    private final AgronomicStatisticRepository agronomicStatisticRepository;

    /**
     * IoT device repository port.
     */
    private final IoTDeviceRepository ioTDeviceRepository;

    /**
     * Application clock used to build deterministic monitoring date ranges.
     */
    private final Clock clock;

    /** Crop-aware NDVI-to-health classifier (shared with the summaries). */
    private final PlotHealthEvaluator plotHealthEvaluator;

    /** Chill-fulfilment-based phenological risk classifier. */
    private final PhenologicalRiskEvaluator phenologicalRiskEvaluator;

    /** Resolves each plot's crop-specific winter-chill requirement. */
    private final ChillRequirementResolver chillRequirementResolver;

    /**
     * Handles the GetPlotById query.
     *
     * @param query The query containing the plot identifier.
     * @return A successful result with the plot, or a not found application error.
     */
    @Transactional(readOnly = true)
    public Result<Plot, ApplicationError> handle(GetPlotByIdQuery query) {
        var plot = plotRepository.findById(new PlotId(query.plotId()));

        if (plot.isEmpty() || !plot.get().isActive()) {
            return Result.failure(ApplicationError.notFound("plot", query.plotId().toString()));
        }

        return Result.success(plot.get());
    }

    /**
     * Handles the query for all active plots owned by a user.
     *
     * @param query The query containing the owner user identifier.
     * @return A successful result with the active plots.
     */
    @Transactional(readOnly = true)
    public Result<List<Plot>, ApplicationError> handle(GetPlotsByUserIdQuery query) {
        return Result.success(plotRepository.findByUserId(new UserId(query.userId())));
    }

    /**
     * Handles the query for active plots enriched with their latest imagery.
     *
     * @param query Query containing the owner user identifier.
     * @return Plot imagery read models. Missing provider data is represented by an empty imagery value.
     */
    @Transactional
    public Result<List<PlotWithCurrentImagery>, ApplicationError> handle(
            GetPlotsWithCurrentImageryQuery query
    ) {
        var plots = plotRepository.findByUserId(new UserId(query.userId()));
        var readModels = plots.stream()
                .map(plot -> new PlotWithCurrentImagery(
                        plot,
                        agroMonitoringImageryService.findCurrentImagery(plot)
                ))
                .toList();

        return Result.success(readModels);
    }

    /**
     * Handles the My Plots overview query.
     *
     * <p>
     * Builds the per-plot monitoring rows (NDVI from satellite imagery with
     * recorded statistics as fallback, chill accumulation, derived health badge,
     * online devices and latest update) plus the summary card values.
     * </p>
     *
     * @param query Query containing the owner user identifier.
     * @return The My Plots overview read model.
     */
    @Transactional
    public Result<MyPlotsOverview, ApplicationError> handle(GetMyPlotsOverviewQuery query) {
        var userId = new UserId(query.userId());
        var plots = plotRepository.findByUserId(userId);
        var plotOverviews = plots.stream()
                .map(plot -> toPlotMonitoringOverview(userId, plot))
                .toList();

        var monitoredArea = plots.stream()
                .map(plot -> plot.getAreaSize().getHectares())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var onlineDevices = plotOverviews.stream()
                .mapToLong(PlotMonitoringOverview::onlineDeviceCount)
                .sum();

        var climateLinkedPlots = plotOverviews.stream()
                .filter(overview ->
                        overview.climateMonitoring() == IntegrationLinkStatus.ACTIVE
                )
                .count();

        return Result.success(new MyPlotsOverview(
                plots.size(),
                monitoredArea,
                Math.toIntExact(climateLinkedPlots),
                onlineDevices,
                plotOverviews
        ));
    }

    private PlotMonitoringOverview toPlotMonitoringOverview(UserId userId, Plot plot) {
        var imagery = agroMonitoringImageryService.findCurrentImagery(plot);

        var endDate = LocalDate.now(clock);
        var dateRange = new DateRange(endDate.minusDays(OVERVIEW_STATISTICS_LOOKBACK_DAYS), endDate);
        var latestStatistic = agronomicStatisticRepository
                .findAllByUserIdAndPlotIdAndMeasurementDateBetween(userId, plot.getId(), dateRange)
                .stream()
                .max(Comparator.comparing(statistic -> statistic.getMeasurementDate().getValue()));

        var currentNdvi = imagery.map(SatelliteImagery::ndviMean)
                .filter(Objects::nonNull)
                .or(() -> latestStatistic.map(statistic -> statistic.getNdviValue().getValue()))
                .orElse(null);

        var chillPortions = latestStatistic
                .map(statistic -> statistic.getChillPortions().getValue())
                .orElse(null);

        var chillRequirement = chillRequirementResolver.resolveFor(plot).value();
        var healthStatus = plotHealthEvaluator.evaluate(currentNdvi, plot.getCropType());
        /* Overview lacks per-plot weather/NDVI history, so risk is chill-only here;
         * the per-plot monitoring summary refines it with anomaly and trend. */
        var phenologicalRisk = phenologicalRiskEvaluator.evaluate(
                chillPortions, chillRequirement, null, false);

        var lastUpdatedAt = latestInstant(
                imagery.map(SatelliteImagery::captureDate),
                latestStatistic.map(AgronomicStatistic::getMeasurementDate)
                        .map(MeasurementDate::getValue)
                        .map(date -> date.atStartOfDay(ZoneOffset.UTC).toInstant())
        );

        var onlineDevices = ioTDeviceRepository.findAllByPlotId(plot.getId().getValue())
                .stream()
                .filter(device -> device.getStatus() == IoTDeviceStatus.ACTIVE)
                .count();

        var linkedToProvider = agroMonitoringImageryService.isPlotLinked(plot);
        var climateMonitoring = linkedToProvider
                ? IntegrationLinkStatus.ACTIVE
                : IntegrationLinkStatus.NOT_LINKED;
        var satelliteNdvi = imagery.isPresent()
                ? IntegrationLinkStatus.ACTIVE
                : linkedToProvider
                ? IntegrationLinkStatus.INITIALIZING
                : IntegrationLinkStatus.NOT_LINKED;

        /* Active alerts are a placeholder until the alerts capability exists. */
        return new PlotMonitoringOverview(
                plot,
                currentNdvi,
                chillPortions,
                healthStatus,
                phenologicalRisk,
                onlineDevices,
                0,
                lastUpdatedAt,
                climateMonitoring,
                satelliteNdvi
        );
    }

    private Instant latestInstant(Optional<Instant> first, Optional<Instant> second) {
        return first
                .map(value -> second.filter(other -> other.isAfter(value)).orElse(value))
                .or(() -> second)
                .orElse(null);
    }

    /**
     * Handles the query for a raster NDVI tile of the current imagery of a plot.
     *
     * <p>
     * Validates plot existence and ownership before proxying the tile from the
     * imagery provider, so provider credentials never reach the client.
     * </p>
     *
     * @param query Query with the requesting user, the plot and the tile coordinates.
     * @return The tile image bytes, or a failure when the plot is invalid, not
     *         owned by the user, or no imagery is available.
     */
    @Transactional(readOnly = true)
    public Result<byte[], ApplicationError> handle(GetPlotNdviTileQuery query) {
        var plot = plotRepository.findById(new PlotId(query.plotId()));

        if (plot.isEmpty() || !plot.get().isActive()) {
            return Result.failure(ApplicationError.notFound("plot", query.plotId().toString()));
        }

        if (!plot.get().belongsTo(new UserId(query.userId()))) {
            return Result.failure(ApplicationError.forbidden(
                    "plot-ownership",
                    "User %d does not own plot %d.".formatted(query.userId(), query.plotId())));
        }

        return agroMonitoringImageryService
                .fetchCurrentNdviTile(plot.get(), query.zoom(), query.x(), query.y())
                .<Result<byte[], ApplicationError>>map(Result::success)
                .orElseGet(() -> Result.failure(ApplicationError.notFound(
                        "plot_imagery_tile",
                        query.plotId().toString())));
    }
}
