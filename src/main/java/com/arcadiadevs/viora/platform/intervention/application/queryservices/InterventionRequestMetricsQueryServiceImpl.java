package com.arcadiadevs.viora.platform.intervention.application.queryservices;

import com.arcadiadevs.viora.platform.intervention.domain.model.queries.GetGrowerRequestMetricsQuery;
import com.arcadiadevs.viora.platform.intervention.domain.model.queries.GetSpecialistRequestMetricsQuery;
import com.arcadiadevs.viora.platform.intervention.application.queryservices.InterventionRequestMetricsQueryService;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.ActiveAlertResource;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.GrowerRequestMetricsResource;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.SpecialistRequestMetricsResource;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link InterventionRequestMetricsQueryService}.
 */
@Service
public class InterventionRequestMetricsQueryServiceImpl implements InterventionRequestMetricsQueryService {

    @Override
    public GrowerRequestMetricsResource handle(GetGrowerRequestMetricsQuery query) {
        // TODO: Implement actual logic using repositories and ACLs
        return new GrowerRequestMetricsResource(
                new ActiveAlertResource(1L, "Placeholder Alert", "Tacna", "4.2 ha"),
                0,
                "PENDING"
        );
    }

    @Override
    public SpecialistRequestMetricsResource handle(GetSpecialistRequestMetricsQuery query) {
        // TODO: Implement actual logic using repositories
        return new SpecialistRequestMetricsResource(0, 0, 0);
    }
}
