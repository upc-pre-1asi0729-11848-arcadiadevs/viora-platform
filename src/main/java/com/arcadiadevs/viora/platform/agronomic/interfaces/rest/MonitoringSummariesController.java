package com.arcadiadevs.viora.platform.agronomic.interfaces.rest;

import com.arcadiadevs.viora.platform.agronomic.application.queryservices.MonitoringSummaryQueryService;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetCurrentMonitoringSummaryQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.MonitoringSummaryResource;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform.MonitoringSummaryResourceFromMonitoringSummaryAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller that exposes monitoring summary endpoints.
 *
 * <p>
 * (TS016TASK006) GET /api/v1/monitoring-summaries/current — retrieves the current
 * monitoring summary for a given user.
 * </p>
 */
@RestController
@RequestMapping(value = "/api/v1/monitoring-summaries", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Monitoring Summaries", description = "Monitoring Summary Management Endpoints")
public class MonitoringSummariesController {

    private final MonitoringSummaryQueryService monitoringSummaryQueryService;
    private final MonitoringSummaryResourceFromMonitoringSummaryAssembler assembler;

    @GetMapping
    @Operation(
            summary = "Get monitoring summaries",
            description = "Retrieves the agronomic monitoring summary for a given user, " +
                    "including health status, NDVI, weather snapshot, climate risk level, " +
                    "and mitigation recommendations. By default, it returns the current (latest) summary."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Monitoring summary retrieved successfully",
                    content = @Content(schema = @Schema(implementation = MonitoringSummaryResource.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid userId parameter"),
            @ApiResponse(responseCode = "404", description = "No monitoring summary found for the given user")
    })
    public ResponseEntity<?> getMonitoringSummaries(
            @Parameter(description = "User identifier", required = true)
            @RequestParam Long userId,
            @Parameter(description = "Limit the number of results (defaults to 1 for current summary)")
            @RequestParam(defaultValue = "1") int limit
    ) {
        // Currently, the query only supports fetching the current one (limit 1 implicitly).
        var query = new GetCurrentMonitoringSummaryQuery(new UserId(userId));
        var result = monitoringSummaryQueryService.handle(query);

        if (result.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(assembler.toResource(result.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
