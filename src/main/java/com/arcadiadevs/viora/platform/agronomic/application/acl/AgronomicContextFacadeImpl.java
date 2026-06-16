package com.arcadiadevs.viora.platform.agronomic.application.acl;

import com.arcadiadevs.viora.platform.agronomic.application.queryservices.PlotMonitoringSummaryQueryService;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotMonitoringSummaryQuery;
import com.arcadiadevs.viora.platform.agronomic.interfaces.acl.AgronomicContextFacade;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Application-layer implementation of the Agronomic ACL facade.
 *
 * <p>Provides a simplified integration surface for other bounded contexts that need agronomic
 * operations without coupling to Agronomic internal models.</p>
 */
@Service
public class AgronomicContextFacadeImpl implements AgronomicContextFacade {

    private final PlotMonitoringSummaryQueryService plotMonitoringSummaryQueryService;

    public AgronomicContextFacadeImpl(PlotMonitoringSummaryQueryService plotMonitoringSummaryQueryService) {
        this.plotMonitoringSummaryQueryService = plotMonitoringSummaryQueryService;
    }

    /**
     * Fetches the current NDVI value for a given plot through the Agronomic query service.
     *
     * @param plotId Plot identifier
     * @param userId Owner user identifier
     * @return current NDVI value if available
     */
    @Override
    public Optional<Double> fetchCurrentNdviByPlotId(Long plotId, Long userId) {
        var query = new GetPlotMonitoringSummaryQuery(userId, plotId);
        var result = plotMonitoringSummaryQueryService.handle(query);
        if (result.isSuccess() && result.value().isPresent()) {
            return Optional.ofNullable(result.value().get().currentNdvi());
        }
        return Optional.empty();
    }
}
