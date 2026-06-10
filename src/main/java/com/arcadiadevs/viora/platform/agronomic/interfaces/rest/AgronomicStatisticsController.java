package com.arcadiadevs.viora.platform.agronomic.interfaces.rest;

import com.arcadiadevs.viora.platform.agronomic.application.queryservices.AgronomicStatisticQueryService;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetAgronomicStatisticsQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.TimeRange;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.AgronomicStatisticResource;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform.AgronomicStatisticResourceFromAgronomicStatisticAssembler;
import com.arcadiadevs.viora.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
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
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Agronomic statistics REST controller.
 *
 * <p>
 * Exposes endpoints to retrieve agronomic statistics for trend visualization.
 * </p>
 */
@RestController
@RequestMapping(value = "/api/v1/agronomic-statistics", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Agronomic Statistics", description = "Agronomic Statistics Management Endpoints")
public class AgronomicStatisticsController {

    private final AgronomicStatisticQueryService agronomicStatisticQueryService;

    @GetMapping
    @Operation(
            summary = "Get agronomic statistics",
            description = "Gets agronomic statistics by user, optionally filtered by plot and constrained by a time range."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Agronomic statistics retrieved successfully",
                    content = @Content(schema = @Schema(implementation = AgronomicStatisticResource.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "403", description = "Authenticated user cannot access the requested statistics")
    })
    public ResponseEntity<?> getAgronomicStatistics(
            @Parameter(description = "User identifier", required = true)
            @RequestParam Long userId,

            @Parameter(description = "Optional plot identifier")
            @RequestParam(required = false) Long plotId,

            @Parameter(
                    description = "Time range. Allowed values: LAST_7_DAYS, LAST_30_DAYS, LAST_90_DAYS, LAST_180_DAYS, LAST_365_DAYS",
                    required = true
            )
            @RequestParam String timeRange,

            @Parameter(description = "Authenticated user identifier")
            @RequestHeader(value = "X-Authenticated-User-Id", required = false) Long authenticatedUserId
    ) {
        var effectiveAuthenticatedUserId = authenticatedUserId != null
                ? authenticatedUserId
                : userId;

        var query = new GetAgronomicStatisticsQuery(
                userId,
                effectiveAuthenticatedUserId,
                plotId,
                TimeRange.from(timeRange)
        );

        var result = agronomicStatisticQueryService.handle(query);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                statistics -> statistics.stream()
                        .map(AgronomicStatisticResourceFromAgronomicStatisticAssembler::toResourceFromAggregate)
                        .toList(),
                HttpStatus.OK
        );
    }
}