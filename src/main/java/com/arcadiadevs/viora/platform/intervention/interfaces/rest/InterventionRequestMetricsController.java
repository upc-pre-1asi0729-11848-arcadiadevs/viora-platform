package com.arcadiadevs.viora.platform.intervention.interfaces.rest;

import com.arcadiadevs.viora.platform.intervention.domain.model.queries.GetGrowerRequestMetricsQuery;
import com.arcadiadevs.viora.platform.intervention.domain.model.queries.GetSpecialistRequestMetricsQuery;
import com.arcadiadevs.viora.platform.intervention.application.queryservices.InterventionRequestMetricsQueryService;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.GrowerRequestMetricsResource;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.SpecialistRequestMetricsResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for intervention request metrics.
 */
@RestController
@RequestMapping(value = "/api/v1/intervention-request-metrics")
@Tag(name = "Intervention Request Metrics", description = "Endpoints for intervention request metrics")
public class InterventionRequestMetricsController {

    private final InterventionRequestMetricsQueryService interventionRequestMetricsQueryService;

    public InterventionRequestMetricsController(InterventionRequestMetricsQueryService interventionRequestMetricsQueryService) {
        this.interventionRequestMetricsQueryService = interventionRequestMetricsQueryService;
    }

    /**
     * Gets the request metrics for a specific grower.
     *
     * @param growerId the ID of the grower
     * @return the metrics resource
     */
    @GetMapping(params = "growerId")
    @Operation(summary = "Get request metrics for a grower")
    public ResponseEntity<GrowerRequestMetricsResource> getGrowerMetrics(@RequestParam Long growerId) {
        var query = new GetGrowerRequestMetricsQuery(growerId);
        var metrics = interventionRequestMetricsQueryService.handle(query);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Gets the request metrics for a specific specialist.
     *
     * @param specialistId the ID of the specialist
     * @return the metrics resource
     */
    @GetMapping(params = "specialistId")
    @Operation(summary = "Get request metrics for a specialist")
    public ResponseEntity<SpecialistRequestMetricsResource> getSpecialistMetrics(@RequestParam Long specialistId) {
        var query = new GetSpecialistRequestMetricsQuery(specialistId);
        var metrics = interventionRequestMetricsQueryService.handle(query);
        return ResponseEntity.ok(metrics);
    }
}
