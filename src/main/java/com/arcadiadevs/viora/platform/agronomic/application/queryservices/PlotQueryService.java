package com.arcadiadevs.viora.platform.agronomic.application.queryservices;

import com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices.AgroMonitoringImageryService;
import com.arcadiadevs.viora.platform.agronomic.application.readmodels.PlotWithCurrentImagery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotByIdQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotNdviTileQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotsByUserIdQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotsWithCurrentImageryQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.PlotRepository;
import com.arcadiadevs.viora.platform.shared.application.result.ApplicationError;
import com.arcadiadevs.viora.platform.shared.application.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    /**
     * Plot repository port.
     */
    private final PlotRepository plotRepository;

    /**
     * Satellite imagery outbound service.
     */
    private final AgroMonitoringImageryService agroMonitoringImageryService;

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
