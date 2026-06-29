package com.arcadiadevs.viora.platform.intervention.domain.services;

import com.arcadiadevs.viora.platform.intervention.domain.model.queries.GetGrowerRequestMetricsQuery;
import com.arcadiadevs.viora.platform.intervention.domain.model.queries.GetSpecialistRequestMetricsQuery;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.GrowerRequestMetricsResource;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.SpecialistRequestMetricsResource;

/**
 * Service to handle queries related to intervention request metrics.
 */
public interface InterventionRequestMetricsQueryService {
    
    GrowerRequestMetricsResource handle(GetGrowerRequestMetricsQuery query);

    SpecialistRequestMetricsResource handle(GetSpecialistRequestMetricsQuery query);
}
