package com.arcadiadevs.viora.platform.intervention.interfaces.rest;

import com.arcadiadevs.viora.platform.intervention.application.queryservices.SpecialistQueryService;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.SpecialistContactResource;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.SpecialistProfileResource;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.transform.SpecialistResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for specialist profiles and gated contact details.
 */
@RestController
@RequestMapping(value = "/api/v1/specialists")
@Tag(name = "Specialists", description = "Endpoints for specialist profiles and contact")
public class SpecialistsController {

    private final SpecialistQueryService specialistQueryService;

    public SpecialistsController(SpecialistQueryService specialistQueryService) {
        this.specialistQueryService = specialistQueryService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a specialist's public profile")
    public ResponseEntity<SpecialistProfileResource> getProfile(@PathVariable Long id) {
        return specialistQueryService.getProfile(id)
                .map(SpecialistResourceAssembler::toProfileResource)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/contact")
    @Operation(summary = "Get a specialist's contact details (only for an accepted request)")
    public ResponseEntity<SpecialistContactResource> getContact(
            @PathVariable Long id,
            @RequestParam Long requestId) {
        return specialistQueryService.getContactForAcceptedRequest(id, requestId)
                .map(SpecialistResourceAssembler::toContactResource)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }
}
