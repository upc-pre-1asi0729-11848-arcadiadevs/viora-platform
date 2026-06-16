package com.arcadiadevs.viora.platform.surveillance.interfaces.rest;

import com.arcadiadevs.viora.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.arcadiadevs.viora.platform.surveillance.application.commandservices.PestSightingCommandService;
import com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources.CreatePestSightingReportResource;
import com.arcadiadevs.viora.platform.surveillance.interfaces.rest.transform.CreatePestSightingReportCommandFromResourceAssembler;
import com.arcadiadevs.viora.platform.surveillance.interfaces.rest.transform.PestSightingReportResourceFromPestSightingReportAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that exposes Pest Sighting Report endpoints.
 */
@RestController
@RequestMapping(value = "/api/v1/pest-sighting-reports", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Pest Sighting Reports", description = "Manual Pest Sighting Reports Management")
public class PestSightingReportsController {

    private final PestSightingCommandService commandService;

    /**
     * Registers a new manual pest sighting report and triggers an automatic biological risk evaluation.
     *
     * @param resource the request body with the sighting details
     * @return 201 Created with PestSightingReportResource
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Create pest sighting report",
            description = "Registers a new manual pest sighting report and triggers an automatic biological risk evaluation."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Report created successfully",
                    content = @Content(schema = @Schema(implementation = com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources.PestSightingReportResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    public ResponseEntity<?> createReport(@Valid @RequestBody CreatePestSightingReportResource resource) {
        var command = CreatePestSightingReportCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = commandService.handle(command);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                PestSightingReportResourceFromPestSightingReportAssembler::toResourceFromAggregate,
                HttpStatus.CREATED
        );
    }
}
