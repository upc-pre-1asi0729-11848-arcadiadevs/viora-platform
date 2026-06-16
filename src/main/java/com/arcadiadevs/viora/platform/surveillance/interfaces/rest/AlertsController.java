package com.arcadiadevs.viora.platform.surveillance.interfaces.rest;

import com.arcadiadevs.viora.platform.surveillance.application.queryservices.AlertQueryService;
import com.arcadiadevs.viora.platform.surveillance.domain.model.queries.GetAlertByIdQuery;
import com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources.AlertResource;
import com.arcadiadevs.viora.platform.surveillance.interfaces.rest.transform.AlertResourceFromAggregateAssembler;
import com.arcadiadevs.viora.platform.surveillance.interfaces.rest.transform.AlertTimelineRecordResourceFromEntityAssembler;
import com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources.AlertTimelineRecordResource;
import com.arcadiadevs.viora.platform.surveillance.application.commandservices.AlertCommandService;
import com.arcadiadevs.viora.platform.surveillance.domain.model.commands.MarkAlertAsReviewedCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
     * Gets the full detail of an Alert including its timeline.
     *
     * @param alertId the ID of the alert
     * @return the alert resource
     */
    @GetMapping("/{alertId}")
    @Operation(summary = "Get alert details", description = "Retrieves the complete data of an alert, including its supporting metrics and timeline history.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Alert found",
                    content = @Content(schema = @Schema(implementation = AlertResource.class))),
            @ApiResponse(responseCode = "404", description = "Alert not found", content = @Content(mediaType = "application/json", schema = @Schema(type = "object", example = "{}")))
    })
    public ResponseEntity<AlertResource> getAlertById(@PathVariable Long alertId) {
        var query = new GetAlertByIdQuery(alertId);
        var alert = alertQueryService.handle(query);

        if (alert.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var resource = AlertResourceFromAggregateAssembler.toResourceFromAggregate(alert.get());
        return ResponseEntity.ok(resource);
    }

    /**
     * Gets the timeline of an Alert.
     *
     * @param alertId the ID of the alert
     * @return the list of timeline records
     */
    @GetMapping("/{alertId}/timeline")
    @Operation(summary = "Get alert timeline", description = "Retrieves only the historical timeline records of an alert.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Timeline retrieved"),
            @ApiResponse(responseCode = "404", description = "Alert not found", content = @Content(mediaType = "application/json", schema = @Schema(type = "object", example = "{}")))
    })
    public ResponseEntity<java.util.List<AlertTimelineRecordResource>> getAlertTimelineById(@PathVariable Long alertId) {
        var query = new GetAlertByIdQuery(alertId);
        var alert = alertQueryService.handle(query);

        if (alert.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var resources = AlertTimelineRecordResourceFromEntityAssembler.toResourceListFromEntities(alert.get().getTimeline());
        return ResponseEntity.ok(resources);
    }

    /**
     * Marks an Alert as reviewed.
     *
     * @param alertId the ID of the alert
     * @return the updated alert resource
     */
    @org.springframework.web.bind.annotation.PatchMapping("/{alertId}/reviewed")
    @Operation(summary = "Mark alert as reviewed", description = "Updates the status of an alert to UNDER_REVIEW and appends a record to its timeline.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Alert marked as reviewed",
                    content = @Content(schema = @Schema(implementation = AlertResource.class))),
            @ApiResponse(responseCode = "400", description = "Alert is already reviewed", content = @Content(mediaType = "application/json", schema = @Schema(type = "object", example = "{}"))),
            @ApiResponse(responseCode = "404", description = "Alert not found", content = @Content(mediaType = "application/json", schema = @Schema(type = "object", example = "{}")))
    })
    public ResponseEntity<AlertResource> markAlertAsReviewed(@PathVariable Long alertId) {
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

        var resource = AlertResourceFromAggregateAssembler.toResourceFromAggregate(alert.get());
        return ResponseEntity.ok(resource);
    }
}
