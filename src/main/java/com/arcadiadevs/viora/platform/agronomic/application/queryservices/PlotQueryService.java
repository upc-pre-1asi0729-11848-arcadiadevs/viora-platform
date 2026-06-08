package com.arcadiadevs.viora.platform.agronomic.application.queryservices;

import com.arcadiadevs.viora.platform.agronomic.domain.exceptions.PlotNotFoundException;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotByIdQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.PlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Plot query service.
 *
 * <p>
 *     Application service responsible for handling read operations related to plots.
 *     It coordinates the query request with the domain repository without exposing
 *     persistence details to the REST layer.
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
     * @return The plot if it exists.
     * @throws PlotNotFoundException If the plot does not exist.
     */
    public Plot handle(GetPlotByIdQuery query) {
        return plotRepository.findById(query.plotId())
                .orElseThrow(() -> new PlotNotFoundException(query.plotId()));
    }
}