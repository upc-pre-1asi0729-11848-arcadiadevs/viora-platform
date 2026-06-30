package com.arcadiadevs.viora.platform.intervention.interfaces.rest;

import com.arcadiadevs.viora.platform.intervention.application.commandservices.ServiceProposalCommandService;
import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.ServiceProposal;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.AcceptServiceProposalCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.RejectServiceProposalCommand;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.ServiceProposalResource;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.SubmitServiceProposalResource;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.UpdateServiceProposalStatusResource;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.transform.ServiceProposalResourceFromEntityAssembler;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.transform.SubmitServiceProposalCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1/service-proposals")
@Tag(name = "Service Proposals", description = "Endpoints for managing service proposals")
public class ServiceProposalsController {

    private final ServiceProposalCommandService commandService;

    public ServiceProposalsController(ServiceProposalCommandService commandService) {
        this.commandService = commandService;
    }

    @PostMapping
    @Operation(summary = "Submit a new service proposal")
    public ResponseEntity<ServiceProposalResource> submitServiceProposal(
            @RequestBody SubmitServiceProposalResource resource) {
        
        var command = SubmitServiceProposalCommandFromResourceAssembler.toCommandFromResource(resource);
        var proposal = commandService.handle(command);
        
        if (proposal.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ServiceProposalResourceFromEntityAssembler.toResourceFromEntity(proposal.get()));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update a service proposal status")
    public ResponseEntity<ServiceProposalResource> updateServiceProposalStatus(
            @PathVariable Long id,
            @RequestBody UpdateServiceProposalStatusResource resource) {
        
        Optional<ServiceProposal> proposal = Optional.empty();
        
        if ("ACCEPTED".equalsIgnoreCase(resource.status())) {
            var command = new AcceptServiceProposalCommand(id);
            proposal = commandService.handle(command);
        } else if ("REJECTED".equalsIgnoreCase(resource.status()) || "DECLINED".equalsIgnoreCase(resource.status())) {
            var command = new RejectServiceProposalCommand(id);
            proposal = commandService.handle(command);
        } else {
            return ResponseEntity.badRequest().build();
        }
        
        if (proposal.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(ServiceProposalResourceFromEntityAssembler.toResourceFromEntity(proposal.get()));
    }
}
