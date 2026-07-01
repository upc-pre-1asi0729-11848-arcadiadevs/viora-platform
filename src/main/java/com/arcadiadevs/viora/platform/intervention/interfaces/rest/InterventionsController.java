package com.arcadiadevs.viora.platform.intervention.interfaces.rest;

import com.arcadiadevs.viora.platform.intervention.application.queryservices.InterventionSummaryQueryService;
import com.arcadiadevs.viora.platform.intervention.domain.model.queries.GetGrowerInterventionsQuery;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.InterventionSummaryResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller exposing the producer-facing Interventions overview: the
 * grower's accepted cases composed with their technical-service lifecycle.
 */
@RestController
@RequestMapping(value = "/api/v1/interventions")
@Tag(name = "Interventions", description = "Producer-facing view of the technical intervention lifecycle")
public class InterventionsController {

    private final InterventionSummaryQueryService interventionSummaryQueryService;

    public InterventionsController(InterventionSummaryQueryService interventionSummaryQueryService) {
        this.interventionSummaryQueryService = interventionSummaryQueryService;
    }

    @GetMapping
    @Operation(summary = "List a grower's interventions with their lifecycle status")
    public ResponseEntity<List<InterventionSummaryResource>> getGrowerInterventions(
            @RequestParam Long growerId) {
        var interventions = interventionSummaryQueryService.handle(new GetGrowerInterventionsQuery(growerId));
        return ResponseEntity.ok(interventions);
    }
}
