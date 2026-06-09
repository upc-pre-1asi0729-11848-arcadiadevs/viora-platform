package com.arcadiadevs.viora.platform.agronomic.interfaces.rest;

import com.arcadiadevs.viora.platform.agronomic.application.queryservices.PlotQueryService;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotByIdQuery;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform.PlotResourceFromPlotAssembler;
import com.arcadiadevs.viora.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Plots REST controller.
 *
 * <p>
 * Exposes endpoints for managing productive agricultural plots
 * in the agronomic bounded context.
 * </p>
 */
@RestController
@RequestMapping(value = "/api/v1/plots", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Plots", description = "Plots Management Endpoints")
public class PlotsController {

    /**
     * Plot query service.
     */
    private final PlotQueryService plotQueryService;

    /**
     * Gets a plot by its ID.
     *
     * @param plotId The plot identifier.
     * @return The plot resource if found, or a standardized error response.
     */
    @GetMapping("/{plotId}")
    @Operation(
            summary = "Get plot by ID",
            description = "Gets the details of a productive agricultural plot by its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plot found"),
            @ApiResponse(responseCode = "400", description = "Invalid plot ID"),
            @ApiResponse(responseCode = "404", description = "Plot not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    public ResponseEntity<?> getPlotById(@PathVariable Long plotId) {
        var getPlotByIdQuery = new GetPlotByIdQuery(plotId);
        var result = plotQueryService.handle(getPlotByIdQuery);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                PlotResourceFromPlotAssembler::toResourceFromAggregate,
                HttpStatus.OK
        );
    }
}