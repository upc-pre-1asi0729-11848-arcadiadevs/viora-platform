package com.arcadiadevs.viora.platform.agronomic.application.queryservices;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotByIdQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.PlotRepository;
import com.arcadiadevs.viora.platform.shared.application.result.ApplicationError;
import com.arcadiadevs.viora.platform.shared.application.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
