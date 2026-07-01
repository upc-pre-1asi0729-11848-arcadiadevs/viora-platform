package com.arcadiadevs.viora.platform.intervention.interfaces.rest;

import com.arcadiadevs.viora.platform.intervention.application.commandservices.SimulateInterventionPrescriptionCommandService;
import com.arcadiadevs.viora.platform.intervention.application.queryservices.InterventionSummaryQueryService;
import com.arcadiadevs.viora.platform.intervention.domain.model.queries.GetGrowerInterventionsQuery;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.InterventionSummaryResource;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.TreatmentPrescriptionResource;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.transform.TreatmentPrescriptionResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
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
    private final SimulateInterventionPrescriptionCommandService simulatePrescriptionCommandService;

    public InterventionsController(
            InterventionSummaryQueryService interventionSummaryQueryService,
            SimulateInterventionPrescriptionCommandService simulatePrescriptionCommandService) {
        this.interventionSummaryQueryService = interventionSummaryQueryService;
        this.simulatePrescriptionCommandService = simulatePrescriptionCommandService;
    }

    @GetMapping
    @Operation(summary = "List a grower's interventions with their lifecycle status")
    public ResponseEntity<List<InterventionSummaryResource>> getGrowerInterventions(
            @RequestParam Long growerId) {
        var interventions = interventionSummaryQueryService.handle(new GetGrowerInterventionsQuery(growerId));
        return ResponseEntity.ok(interventions);
    }

    @PostMapping("/{requestId}/simulate-prescription")
    @Operation(summary = "Simulate the specialist issuing a technical prescription for an accepted case")
    public ResponseEntity<TreatmentPrescriptionResource> simulatePrescription(@PathVariable Long requestId) {
        return simulatePrescriptionCommandService.simulateForRequest(requestId)
                .map(TreatmentPrescriptionResourceAssembler::toResourceFromDomain)
                .map(resource -> ResponseEntity.status(HttpStatus.CREATED).body(resource))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }
}
