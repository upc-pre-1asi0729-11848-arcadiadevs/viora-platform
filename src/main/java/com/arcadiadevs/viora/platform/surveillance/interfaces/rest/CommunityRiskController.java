package com.arcadiadevs.viora.platform.surveillance.interfaces.rest;

import com.arcadiadevs.viora.platform.surveillance.application.queryservices.CommunityRiskQueryService;
import com.arcadiadevs.viora.platform.surveillance.domain.model.queries.GetCommunityRiskByPlotQuery;
import com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources.CommunityRiskResource;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for the anonymized Community Risk snapshot.
 */
@RestController
@RequestMapping("/api/v1/community-risk")
@RequiredArgsConstructor
@Tag(name = "Community Risk", description = "Anonymized nearby risk signals around a plot")
public class CommunityRiskController {

    private final CommunityRiskQueryService communityRiskQueryService;

    /**
     * Gets the community-risk snapshot around a reference plot.
     *
     * @param plotId   the reference plot identifier
     * @param radiusKm the monitoring radius in kilometers (defaults to 10)
     * @return the community-risk snapshot, or 404 if the plot does not exist
     */
    @GetMapping
    @Operation(
            summary = "Get community risk around a plot",
            description = "Returns anonymized nearby risk signals (derived from active alerts on "
                    + "neighbor plots within the radius) plus preventive recommendations."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Community risk snapshot retrieved",
                    content = @Content(schema = @Schema(implementation = CommunityRiskResource.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "404", description = "Reference plot not found")
    })
    public ResponseEntity<CommunityRiskResource> getCommunityRisk(
            @Parameter(description = "Reference plot identifier", required = true)
            @RequestParam Long plotId,
            @Parameter(description = "Monitoring radius in kilometers")
            @RequestParam(defaultValue = "10") double radiusKm
    ) {
        var query = new GetCommunityRiskByPlotQuery(plotId, radiusKm);

        return communityRiskQueryService.handle(query)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
