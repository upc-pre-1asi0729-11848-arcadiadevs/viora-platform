package com.arcadiadevs.viora.platform.intervention.interfaces.rest;

import com.arcadiadevs.viora.platform.intervention.application.commandservices.InterventionExecutionCommandService;
import com.arcadiadevs.viora.platform.intervention.application.queryservices.InterventionExecutionQueryService;
import com.arcadiadevs.viora.platform.intervention.domain.model.queries.GetInterventionExecutionByIdQuery;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.CertifyApplicationResource;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.InterventionExecutionResource;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.transform.InterventionExecutionResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/intervention-executions")
@Tag(name = "Intervention Executions", description = "Endpoints for certifying application executions")
public class InterventionExecutionsController {

    private final InterventionExecutionCommandService commandService;
    private final InterventionExecutionQueryService queryService;

    public InterventionExecutionsController(InterventionExecutionCommandService commandService, InterventionExecutionQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping
    @Operation(summary = "Certify an agrochemical application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Application certified successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or application already certified")
    })
    public ResponseEntity<InterventionExecutionResource> certifyApplication(@RequestBody CertifyApplicationResource resource) {
        var command = InterventionExecutionResourceAssembler.toCommandFromResource(resource);
        var execution = commandService.handle(command);
        
        if (execution.isEmpty()) return ResponseEntity.badRequest().build();
        
        var executionResource = InterventionExecutionResourceAssembler.toResourceFromDomain(execution.get());
        return new ResponseEntity<>(executionResource, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an intervention execution certification by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Execution certification found"),
            @ApiResponse(responseCode = "404", description = "Execution certification not found")
    })
    public ResponseEntity<InterventionExecutionResource> getInterventionExecutionById(@PathVariable Long id) {
        var query = new GetInterventionExecutionByIdQuery(id);
        var execution = queryService.handle(query);
        
        if (execution.isEmpty()) return ResponseEntity.notFound().build();
        
        var executionResource = InterventionExecutionResourceAssembler.toResourceFromDomain(execution.get());
        return ResponseEntity.ok(executionResource);
    }
}
