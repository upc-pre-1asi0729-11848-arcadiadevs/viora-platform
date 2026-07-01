package com.arcadiadevs.viora.platform.intervention.interfaces.rest;

import com.arcadiadevs.viora.platform.intervention.domain.exceptions.InterventionRequestNotFoundException;
import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.InterventionRequest;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.CreateInterventionRequestCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.DeclineInterventionRequestCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.SubmitServiceProposalCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.queries.GetGrowerInterventionRequestsQuery;
import com.arcadiadevs.viora.platform.intervention.domain.model.queries.GetInterventionRequestByIdQuery;
import com.arcadiadevs.viora.platform.intervention.application.commandservices.InterventionRequestCommandService;
import com.arcadiadevs.viora.platform.intervention.application.commandservices.ServiceProposalCommandService;
import com.arcadiadevs.viora.platform.intervention.application.queryservices.InterventionRequestQueryService;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.CreateInterventionRequestResource;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.DeclineInterventionRequestResource;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.InterventionRequestResource;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.transform.CreateInterventionRequestCommandFromResourceAssembler;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.transform.DeclineInterventionRequestCommandFromResourceAssembler;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.transform.InterventionRequestResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/**
 * REST controller for Intervention Requests.
 */
@RestController
@RequestMapping(value = "/api/v1/intervention-requests")
@Tag(name = "Intervention Requests", description = "Endpoints for managing intervention requests")
public class InterventionRequestsController {

    private final InterventionRequestCommandService commandService;
    private final InterventionRequestQueryService queryService;
    private final ServiceProposalCommandService serviceProposalCommandService;

    public InterventionRequestsController(
            InterventionRequestCommandService commandService,
            InterventionRequestQueryService queryService,
            ServiceProposalCommandService serviceProposalCommandService) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.serviceProposalCommandService = serviceProposalCommandService;
    }

    /**
     * Creates a new intervention request.
     *
     * @param resource the resource containing request details
     * @return the created intervention request
     */
    @PostMapping
    @Operation(summary = "Create a new intervention request")
    public ResponseEntity<InterventionRequestResource> createInterventionRequest(
            @RequestBody CreateInterventionRequestResource resource) {
        
        var command = CreateInterventionRequestCommandFromResourceAssembler.toCommandFromResource(resource);
        
        var request = commandService.handle(command);
        if (request.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(InterventionRequestResourceFromEntityAssembler.toResourceFromEntity(request.get()));
    }

    /**
     * Lists a grower's intervention requests, optionally scoped to a single plot.
     * Powers the "My assistance requests" history for the selected plot.
     *
     * @param growerId the grower whose requests are listed
     * @param plotId   optional plot filter; when omitted the full history is returned
     * @return the grower's intervention requests
     */
    @GetMapping
    @Operation(summary = "List a grower's intervention requests (optionally by plot)")
    public ResponseEntity<List<InterventionRequestResource>> getGrowerInterventionRequests(
            @RequestParam Long growerId,
            @RequestParam(required = false) Long plotId) {
        var query = new GetGrowerInterventionRequestsQuery(growerId, plotId);
        var requests = queryService.handle(query).stream()
                .map(InterventionRequestResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(requests);
    }

    /**
     * Gets an intervention request by ID.
     *
     * @param id the request ID
     * @return the intervention request
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get an intervention request by ID")
    public ResponseEntity<InterventionRequestResource> getInterventionRequestById(@PathVariable Long id) {
        var query = new GetInterventionRequestByIdQuery(id);
        var request = queryService.handle(query);
        
        if (request.isEmpty()) {
            throw new InterventionRequestNotFoundException(id);
        }
        
        return ResponseEntity.ok(InterventionRequestResourceFromEntityAssembler.toResourceFromEntity(request.get()));
    }

    /**
     * Declines an intervention request.
     *
     * @param id       the request ID
     * @param resource the resource containing the decline reason
     * @return the updated intervention request
     */
    @PatchMapping("/{id}")
    @Operation(summary = "Decline an intervention request")
    public ResponseEntity<InterventionRequestResource> declineInterventionRequest(
            @PathVariable Long id,
            @RequestBody DeclineInterventionRequestResource resource) {
        
        var command = DeclineInterventionRequestCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var request = commandService.handle(command);
        
        if (request.isEmpty()) {
            throw new InterventionRequestNotFoundException(id);
        }

        return ResponseEntity.ok(InterventionRequestResourceFromEntityAssembler.toResourceFromEntity(request.get()));
    }

    /**
     * Simulates the specialist responding to a request: submits a service proposal
     * on their behalf, which moves the request to PROPOSAL_RECEIVED. Exists because
     * there is no specialist-facing application yet; it lets the full case flow be
     * exercised end-to-end.
     *
     * @param id the intervention request to respond to
     * @return the generated proposal's linked request, refreshed
     */
    @PostMapping("/{id}/simulate-specialist-response")
    @Operation(summary = "Simulate a specialist submitting a proposal for the request")
    public ResponseEntity<InterventionRequestResource> simulateSpecialistResponse(@PathVariable Long id) {
        var request = queryService.handle(new GetInterventionRequestByIdQuery(id));
        if (request.isEmpty()) {
            throw new InterventionRequestNotFoundException(id);
        }

        var specialistId = request.get().getSpecialistId();
        var command = new SubmitServiceProposalCommand(
                id,
                specialistId,
                "Field inspection and phytosanitary evaluation",
                "2-3 hours",
                List.of(
                        "Inspect affected zones in the plot",
                        "Validate symptom report in field",
                        "Review low-vigor areas",
                        "Recommend next technical action"
                ),
                Date.from(Instant.now().plus(3, ChronoUnit.DAYS)),
                280.0,
                "PEN",
                "The initial visit will focus on confirming the probable biological threat "
                        + "and defining whether a technical prescription is required."
        );

        serviceProposalCommandService.handle(command);

        var refreshed = queryService.handle(new GetInterventionRequestByIdQuery(id));
        return ResponseEntity.ok(
                InterventionRequestResourceFromEntityAssembler.toResourceFromEntity(refreshed.orElseThrow()));
    }
}
