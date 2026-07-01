package com.arcadiadevs.viora.platform.intervention.interfaces.rest;

import com.arcadiadevs.viora.platform.intervention.application.commandservices.InterventionOutcomeCommandService;
import com.arcadiadevs.viora.platform.intervention.application.queryservices.InterventionOutcomeQueryService;
import com.arcadiadevs.viora.platform.intervention.domain.model.queries.GetInterventionOutcomeByIdQuery;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.CloseInterventionResource;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.InterventionOutcomeResource;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.ReportInterventionImpactResource;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.transform.InterventionOutcomeResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/intervention-outcomes")
@Tag(name = "Intervention Outcomes", description = "Endpoints for reporting impact and closing interventions")
public class InterventionOutcomesController {

    private final InterventionOutcomeCommandService commandService;
    private final InterventionOutcomeQueryService queryService;

    public InterventionOutcomesController(InterventionOutcomeCommandService commandService, InterventionOutcomeQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping
    @Operation(summary = "Report intervention impact")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Impact reported successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or outcome already reported")
    })
    public ResponseEntity<InterventionOutcomeResource> reportImpact(@RequestBody ReportInterventionImpactResource resource) {
        var command = InterventionOutcomeResourceAssembler.toCommandFromResource(resource);
        var outcome = commandService.handle(command);

        if (outcome.isEmpty()) return ResponseEntity.badRequest().build();

        var outcomeResource = InterventionOutcomeResourceAssembler.toResourceFromDomain(outcome.get());
        return new ResponseEntity<>(outcomeResource, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/evaluation")
    @Operation(summary = "Close intervention with service evaluation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Intervention closed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or already closed"),
            @ApiResponse(responseCode = "404", description = "Intervention outcome not found")
    })
    public ResponseEntity<InterventionOutcomeResource> closeIntervention(@PathVariable Long id, @RequestBody CloseInterventionResource resource) {
        var command = InterventionOutcomeResourceAssembler.toCommandFromResource(id, resource);
        var outcome = commandService.handle(command);

        if (outcome.isEmpty()) return ResponseEntity.badRequest().build();

        var outcomeResource = InterventionOutcomeResourceAssembler.toResourceFromDomain(outcome.get());
        return ResponseEntity.ok(outcomeResource);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an intervention outcome by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Outcome found"),
            @ApiResponse(responseCode = "404", description = "Outcome not found")
    })
    public ResponseEntity<InterventionOutcomeResource> getInterventionOutcomeById(@PathVariable Long id) {
        var query = new GetInterventionOutcomeByIdQuery(id);
        var outcome = queryService.handle(query);

        if (outcome.isEmpty()) return ResponseEntity.notFound().build();

        var outcomeResource = InterventionOutcomeResourceAssembler.toResourceFromDomain(outcome.get());
        return ResponseEntity.ok(outcomeResource);
    }
}
