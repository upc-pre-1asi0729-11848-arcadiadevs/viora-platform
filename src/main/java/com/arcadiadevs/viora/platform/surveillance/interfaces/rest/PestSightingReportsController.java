package com.arcadiadevs.viora.platform.surveillance.interfaces.rest;

import com.arcadiadevs.viora.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.arcadiadevs.viora.platform.surveillance.application.commandservices.PestSightingCommandService;
import com.arcadiadevs.viora.platform.surveillance.application.queryservices.PestSightingReportQueryService;
import com.arcadiadevs.viora.platform.surveillance.domain.model.commands.ReviewPestSightingReportCommand;
import com.arcadiadevs.viora.platform.surveillance.domain.model.queries.GetPestSightingReportsByUserQuery;
import com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources.CreatePestSightingReportResource;
import com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources.ReviewPestSightingReportResource;
import com.arcadiadevs.viora.platform.surveillance.interfaces.rest.transform.CreatePestSightingReportCommandFromResourceAssembler;
import com.arcadiadevs.viora.platform.surveillance.interfaces.rest.transform.PestSightingReportResourceFromPestSightingReportAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    private final PestSightingReportQueryService queryService;

    /**
     * Lists the pest sighting reports submitted by a user (symptom report history).
     *
     * @param reporterUserId the reporter user identifier
     * @return the user's reports, newest first
     */
    @GetMapping
    @Operation(
            summary = "Get pest sighting reports",
            description = "Returns the symptom report history submitted by the given user, newest first."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reports retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    public ResponseEntity<?> getReports(
            @Parameter(description = "Reporter user identifier", required = true)
            @RequestParam Long reporterUserId
    ) {
        var reports = queryService.handle(new GetPestSightingReportsByUserQuery(reporterUserId));
        return ResponseEntity.ok(reports);
    }

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

    /**
     * Resolves a report after a field inspection: confirms the threat (escalating to a
     * high-priority alert) or rules it out as a verified false positive.
     *
     * @param reportId       the report to resolve
     * @param reporterUserId the reporter resolving it (must own the report)
     * @param resource       the inspection outcome (CONFIRMED or RULED_OUT)
     * @return 200 OK with the updated PestSightingReportResource
     */
    @PatchMapping(value = "/{reportId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Resolve pest sighting report after inspection",
            description = "Confirms the threat (raising a high-priority alert) or rules it out as a "
                    + "verified false positive, after the grower inspects the plot."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Report resolved successfully",
                    content = @Content(schema = @Schema(implementation = com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources.PestSightingReportResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid outcome or report not awaiting inspection"),
            @ApiResponse(responseCode = "404", description = "Report not found")
    })
    public ResponseEntity<?> reviewReport(
            @Parameter(description = "Report identifier", required = true)
            @PathVariable Long reportId,

            @Parameter(description = "Reporter user identifier", required = true)
            @RequestParam Long reporterUserId,

            @RequestBody ReviewPestSightingReportResource resource
    ) {
        var command = new ReviewPestSightingReportCommand(reportId, reporterUserId, resource.outcome());
        var result = commandService.handle(command);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                PestSightingReportResourceFromPestSightingReportAssembler::toResourceFromAggregate,
                HttpStatus.OK
        );
    }
}
