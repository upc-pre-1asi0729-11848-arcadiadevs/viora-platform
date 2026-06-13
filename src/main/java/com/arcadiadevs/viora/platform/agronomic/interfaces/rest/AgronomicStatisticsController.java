package com.arcadiadevs.viora.platform.agronomic.interfaces.rest;

import com.arcadiadevs.viora.platform.agronomic.application.commandservices.AgronomicStatisticIngestionService;
import com.arcadiadevs.viora.platform.agronomic.application.queryservices.AgronomicStatisticQueryService;
import com.arcadiadevs.viora.platform.agronomic.application.queryservices.AgronomicStatisticSeriesQueryService;
import com.arcadiadevs.viora.platform.agronomic.application.readmodels.AgronomicStatisticsIngestionReport;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.IngestAgronomicStatisticsCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetAgronomicStatisticSeriesQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetAgronomicStatisticsQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.TimeRange;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.AgronomicStatisticResource;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.AgronomicStatisticSeriesResource;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.AgronomicStatisticsIngestionReportResource;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform.AgronomicStatisticResourceFromAgronomicStatisticAssembler;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform.AgronomicStatisticSeriesResourceAssembler;
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
 * Exposes endpoints to retrieve agronomic statistics, build chart-oriented
 * trend series, and ingest real statistic snapshots on demand.
 * </p>
 */
@RestController
@RequestMapping(value = "/api/v1/agronomic-statistics", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Agronomic Statistics", description = "Agronomic Statistics Management Endpoints")
public class AgronomicStatisticsController {

    private final AgronomicStatisticQueryService agronomicStatisticQueryService;
    private final AgronomicStatisticSeriesQueryService agronomicStatisticSeriesQueryService;
    private final AgronomicStatisticIngestionService agronomicStatisticIngestionService;

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
                    description = "Time range. Allowed values: LAST_7_DAYS, LAST_30_DAYS, LAST_90_DAYS, LAST_180_DAYS, LAST_365_DAYS, CAMPAIGN",
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

    @GetMapping("/series")
    @Operation(
            summary = "Get agronomic statistic trend series",
            description = "Builds a chart-oriented series (NDVI and chill) for a time range with the "
                    + "difference relative to the previous comparable period."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Series retrieved successfully",
                    content = @Content(schema = @Schema(implementation = AgronomicStatisticSeriesResource.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "403", description = "Authenticated user cannot access the requested statistics")
    })
    public ResponseEntity<?> getAgronomicStatisticSeries(
            @Parameter(description = "User identifier", required = true)
            @RequestParam Long userId,

            @Parameter(description = "Optional plot identifier")
            @RequestParam(required = false) Long plotId,

            @Parameter(
                    description = "Time range. Allowed values: LAST_7_DAYS, LAST_30_DAYS, LAST_90_DAYS, LAST_180_DAYS, LAST_365_DAYS, CAMPAIGN",
                    required = true
            )
            @RequestParam String timeRange,

            @Parameter(description = "Authenticated user identifier")
            @RequestHeader(value = "X-Authenticated-User-Id", required = false) Long authenticatedUserId
    ) {
        var effectiveAuthenticatedUserId = authenticatedUserId != null
                ? authenticatedUserId
                : userId;

        var query = new GetAgronomicStatisticSeriesQuery(
                userId,
                effectiveAuthenticatedUserId,
                plotId,
                TimeRange.from(timeRange)
        );

        var result = agronomicStatisticSeriesQueryService.handle(query);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                AgronomicStatisticSeriesResourceAssembler::toResourceFromReadModel,
                HttpStatus.OK
        );
    }

    @PostMapping("/ingest")
    @Operation(
            summary = "Ingest agronomic statistic snapshots",
            description = "Ingests today's snapshot (real NDVI plus weather-derived chill) for each active "
                    + "plot of the user. Idempotent: a plot already snapshotted today is skipped."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Ingestion completed",
                    content = @Content(schema = @Schema(implementation = AgronomicStatisticsIngestionReportResource.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    public ResponseEntity<?> ingestAgronomicStatistics(
            @Parameter(description = "User identifier", required = true)
            @RequestParam Long userId
    ) {
        var result = agronomicStatisticIngestionService.handle(new IngestAgronomicStatisticsCommand(userId));

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                AgronomicStatisticsController::toReportResource,
                HttpStatus.OK
        );
    }

    private static AgronomicStatisticsIngestionReportResource toReportResource(
            AgronomicStatisticsIngestionReport report
    ) {
        return new AgronomicStatisticsIngestionReportResource(report.ingested(), report.skipped());
    }
}
