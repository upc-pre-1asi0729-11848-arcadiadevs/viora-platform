package com.arcadiadevs.viora.platform.surveillance.interfaces.rest;

import com.arcadiadevs.viora.platform.surveillance.application.queryservices.AlertQueryService;
import com.arcadiadevs.viora.platform.surveillance.domain.model.queries.GetAlertByIdQuery;
import com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources.AlertResource;
import com.arcadiadevs.viora.platform.surveillance.interfaces.rest.transform.AlertResourceFromAggregateAssembler;
import com.arcadiadevs.viora.platform.surveillance.interfaces.rest.transform.AlertTimelineRecordResourceFromEntityAssembler;
import com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources.AlertTimelineRecordResource;
import com.arcadiadevs.viora.platform.surveillance.application.commandservices.AlertCommandService;
import com.arcadiadevs.viora.platform.surveillance.domain.model.commands.MarkAlertAsReviewedCommand;
import com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources.UpdateAlertResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for managing Alerts.
 */
@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
@Tag(name = "Alerts", description = "Endpoints for retrieving active and historical threats (alerts)")
public class AlertsController {

    private final AlertQueryService alertQueryService;
    private final AlertCommandService alertCommandService;

    /**
     * Gets a collection of alerts.
     *
     * @param userId the ID of the user
     * @param sort the sorting criteria (e.g. "recent")
     * @param limit the maximum number of alerts to return
     * @return the list of alerts
     */
    @GetMapping
    @Operation(summary = "Get alerts", description = "Retrieves alerts for the given user. Use ?sort=recent to get the most recent alerts matching the dashboard overview.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Alerts retrieved successfully")
    })
    public ResponseEntity<?> getAlerts(
            @Parameter(description = "User identifier", required = true)
            @RequestParam Long userId,
            
            @Parameter(description = "Sorting criteria (e.g., 'recent')")
            @RequestParam(required = false) String sort,
            
            @Parameter(description = "Limit the number of results")
            @RequestParam(defaultValue = "3") int limit
    ) {
        if ("recent".equalsIgnoreCase(sort)) {
            var query = new com.arcadiadevs.viora.platform.surveillance.domain.model.queries.GetRecentAlertsByUserIdQuery(userId, limit);
            var summaries = alertQueryService.handle(query);
            return ResponseEntity.ok(summaries);
        }

        // Return empty list if no specific sort/query is supported yet for all alerts
        return ResponseEntity.ok(java.util.List.of());
    }

    /**
     * Gets the full detail of an Alert or its specific views.
     *
     * @param alertId the ID of the alert
     * @param view the projection view (e.g. "timeline")
     * @return the alert resource or its timeline
     */
    @GetMapping("/{alertId}")
    @Operation(summary = "Get alert details or timeline", description = "Retrieves the complete data of an alert. Use ?view=timeline to get only the historical records.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Alert found or timeline retrieved"),
            @ApiResponse(responseCode = "404", description = "Alert not found", content = @Content(mediaType = "application/json", schema = @Schema(type = "object", example = "{}")))
    })
    public ResponseEntity<?> getAlert(
            @Parameter(description = "Alert identifier", required = true)
            @PathVariable Long alertId,
            
            @Parameter(description = "Projection view. Supported values: 'timeline'")
            @RequestParam(required = false) String view
    ) {
        var query = new GetAlertByIdQuery(alertId);
        var alert = alertQueryService.handle(query);

        if (alert.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if ("timeline".equalsIgnoreCase(view)) {
            var resources = AlertTimelineRecordResourceFromEntityAssembler.toResourceListFromEntities(alert.get().getTimeline());
            return ResponseEntity.ok(resources);
        }

        var resource = AlertResourceFromAggregateAssembler.toResourceFromAggregate(alert.get());
        return ResponseEntity.ok(resource);
    }

    /**
     * Updates an existing alert (e.g., marking as reviewed).
     *
     * @param alertId the ID of the alert
     * @param resource the update resource payload
     * @return the updated alert resource
     */
    @PatchMapping("/{alertId}")
    @Operation(summary = "Update alert status", description = "Partially updates an alert. For example, pass {\"status\": \"UNDER_REVIEW\"} to mark it as reviewed.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Alert updated successfully",
                    content = @Content(schema = @Schema(implementation = AlertResource.class))),
            @ApiResponse(responseCode = "400", description = "Alert is already reviewed or invalid status", content = @Content(mediaType = "application/json", schema = @Schema(type = "object", example = "{}"))),
            @ApiResponse(responseCode = "404", description = "Alert not found", content = @Content(mediaType = "application/json", schema = @Schema(type = "object", example = "{}")))
    })
    public ResponseEntity<AlertResource> updateAlert(
            @Parameter(description = "Alert identifier", required = true)
            @PathVariable Long alertId,
            
            @RequestBody UpdateAlertResource resource
    ) {
        // Evaluate the requested state change
        if ("UNDER_REVIEW".equalsIgnoreCase(resource.status())) {
            var command = new MarkAlertAsReviewedCommand(alertId);
            var result = alertCommandService.handle(command);

            if (result.isFailure()) {
                if (result.failure().get().message().contains("not found")) {
                    return ResponseEntity.notFound().build();
                }
                return ResponseEntity.badRequest().build();
            }

            var query = new GetAlertByIdQuery(result.success().get());
            var alert = alertQueryService.handle(query);

            var responseResource = AlertResourceFromAggregateAssembler.toResourceFromAggregate(alert.get());
            return ResponseEntity.ok(responseResource);
        }

        // Return bad request if the status is not supported for updating
        return ResponseEntity.badRequest().build();
    }
}
