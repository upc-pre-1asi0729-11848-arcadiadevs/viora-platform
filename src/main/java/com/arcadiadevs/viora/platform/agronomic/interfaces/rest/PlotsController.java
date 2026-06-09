package com.arcadiadevs.viora.platform.agronomic.interfaces.rest;

import com.arcadiadevs.viora.platform.agronomic.application.commandservices.PlotCommandService;
import com.arcadiadevs.viora.platform.agronomic.application.queryservices.PlotQueryService;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.DeletePlotCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotByIdQuery;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.UpdatePlotResource;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform.PlotResourceFromPlotAssembler;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform.UpdatePlotCommandFromResourceAssembler;
import com.arcadiadevs.viora.platform.shared.interfaces.rest.resources.MessageResource;
import com.arcadiadevs.viora.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Plots REST controller.
 *
 * <p>
 *     Exposes endpoints for managing productive agricultural plots
 *     in the agronomic bounded context.
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
     * Plot command service.
     */
    private final PlotCommandService plotCommandService;

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

    /**
     * Updates a plot by its ID.
     *
     * @param plotId The plot identifier.
     * @param resource The update plot request body.
     * @return The updated plot resource if successful, or a standardized error response.
     */
    @PatchMapping(value = "/{plotId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Update plot",
            description = "Updates the information and/or geographic boundary of an existing productive agricultural plot."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plot updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Plot not found"),
            @ApiResponse(responseCode = "409", description = "Plot conflict"),
            @ApiResponse(responseCode = "422", description = "Business rule violation"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    public ResponseEntity<?> updatePlot(
            @PathVariable Long plotId,
            @Valid @RequestBody UpdatePlotResource resource
    ) {
        var updatePlotCommand = UpdatePlotCommandFromResourceAssembler.toCommandFromResource(plotId, resource);
        var result = plotCommandService.handle(updatePlotCommand);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                PlotResourceFromPlotAssembler::toResourceFromAggregate,
                HttpStatus.OK
        );
    }

    /**
     * Deletes a plot by its ID.
     *
     * @param plotId The plot identifier.
     * @return A success message if deleted, or a standardized error response.
     */
    @DeleteMapping("/{plotId}")
    @Operation(
            summary = "Delete plot",
            description = "Deletes an existing productive agricultural plot by its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plot deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid plot ID"),
            @ApiResponse(responseCode = "404", description = "Plot not found"),
            @ApiResponse(responseCode = "422", description = "Business rule violation"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    public ResponseEntity<?> deletePlot(@PathVariable Long plotId) {
        var deletePlotCommand = new DeletePlotCommand(plotId);
        var result = plotCommandService.handle(deletePlotCommand);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                MessageResource::new,
                HttpStatus.OK
        );
    }
}
