package com.arcadiadevs.viora.platform.surveillance.infrastructure.outbound.agronomic;

import com.arcadiadevs.viora.platform.agronomic.application.queryservices.PlotQueryService;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotsByUserIdQuery;
import com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources.PlotSummaryResource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Anti-Corruption Layer (Facade) to interact with the Agronomic Bounded Context.
 */
@Service
@RequiredArgsConstructor
public class AgronomicContextFacade {

    private final PlotQueryService plotQueryService;

    /**
     * Gets a map of PlotId -> PlotSummaryResource for all plots owned by a user.
     * This map is useful for efficiently assembling Alert resources.
     *
     * @param userId the user ID
     * @return a map of Plot ID to PlotSummaryResource
     */
    public Map<Long, PlotSummaryResource> getPlotsForUserAsMap(Long userId) {
        var query = new GetPlotsByUserIdQuery(userId);
        var result = plotQueryService.handle(query);

        if (result.isFailure()) {
            return Map.of(); // Return empty map if user has no plots or error occurs
        }

        return result.success().get().stream()
                .collect(Collectors.toMap(
                        plot -> plot.getId().getValue(),
                        plot -> new PlotSummaryResource(
                                plot.getName().getValue(),
                                plot.getLocation(),
                                plot.getAreaSize().getHectares().doubleValue()
                        )
                ));
    }
}
