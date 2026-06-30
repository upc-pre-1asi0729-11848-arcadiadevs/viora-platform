package com.arcadiadevs.viora.platform.intervention.interfaces.rest;

import com.arcadiadevs.viora.platform.intervention.domain.exceptions.InterventionRequestNotFoundException;
import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.InterventionRequest;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.CreateInterventionRequestCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.DeclineInterventionRequestCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.queries.GetInterventionRequestByIdQuery;
import com.arcadiadevs.viora.platform.intervention.application.commandservices.InterventionRequestCommandService;
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

/**
 * REST controller for Intervention Requests.
 */
@RestController
@RequestMapping(value = "/api/v1/intervention-requests")
@Tag(name = "Intervention Requests", description = "Endpoints for managing intervention requests")
public class InterventionRequestsController {

    private final InterventionRequestCommandService commandService;
    private final InterventionRequestQueryService queryService;

    public InterventionRequestsController(
            InterventionRequestCommandService commandService,
            InterventionRequestQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
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
}
