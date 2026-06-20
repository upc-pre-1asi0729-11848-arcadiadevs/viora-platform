package com.arcadiadevs.viora.platform.agronomic.interfaces.rest;

import com.arcadiadevs.viora.platform.agronomic.application.commandservices.PlotCommandService;
import com.arcadiadevs.viora.platform.agronomic.application.queryservices.PlotDetailQueryService;
import com.arcadiadevs.viora.platform.agronomic.application.queryservices.PlotMonitoringSummaryQueryService;
import com.arcadiadevs.viora.platform.agronomic.application.queryservices.PlotQueryService;
import com.arcadiadevs.viora.platform.agronomic.application.queryservices.PlotWeatherForecastQueryService;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetMyPlotsOverviewQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotDetailQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotMonitoringSummaryQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotWeatherForecastQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotsByUserIdQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotsWithCurrentImageryQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.ConfigureChillRequirementCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.DeletePlotCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.ResetChillRequirementCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotByIdQuery;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.ChillRequirementResource;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.ConfigureChillRequirementResource;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.CreatePlotResource;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.MyPlotsOverviewResource;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.PlotDetailResource;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.PlotMonitoringSummaryResource;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.PlotRegistrationResource;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.PlotWeatherForecastResource;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.PlotWithCurrentImageryResource;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.UpdatePlotResource;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform.ChillRequirementResourceAssembler;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform.ConfigureChillRequirementCommandFromResourceAssembler;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform.CreatePlotCommandFromResourceAssembler;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform.MyPlotsOverviewResourceAssembler;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.*;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.*;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform.*;
import com.arcadiadevs.viora.platform.shared.interfaces.rest.resources.MessageResource;
import com.arcadiadevs.viora.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import org.springframework.web.bind.annotation.*;

/**
 * Plots REST controller.
 *
 * <p>
 * Exposes endpoints to manage agronomic plots. This controller uses a clean REST architecture
 * where the Plot is treated as an autonomous Aggregate Root. Projections (views) such as
 * details, monitoring summaries, and weather forecasts are handled via the `view` query parameter.
 * </p>
 */
@RestController
@RequestMapping(value = "/api/v1/plots", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Plots", description = "Plots Management Endpoints")
public class PlotsController {

    private final PlotQueryService plotQueryService;
    private final PlotDetailQueryService plotDetailQueryService;
    private final PlotMonitoringSummaryQueryService plotMonitoringSummaryQueryService;
    private final PlotWeatherForecastQueryService plotWeatherForecastQueryService;
    private final PlotCommandService plotCommandService;

    /**
     * Registers a productive agricultural plot.
     *
     * @param resource The new plot request body.
     * @return The persisted plot resource.
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Create plot",
            description = "Registers a productive agricultural plot and its geographic boundary. "
                    + "Polygon points use GeoJSON order: [longitude, latitude]. "
                    + "The plot area is calculated by the backend from the boundary."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Plot created", content = @Content(schema = @Schema(implementation = PlotRegistrationResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Plot name conflict"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    public ResponseEntity<?> createPlot(@Valid @RequestBody CreatePlotResource resource) {
        var command = CreatePlotCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = plotCommandService.handle(command);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                PlotRegistrationResourceAssembler::toResourceFromReadModel,
                HttpStatus.CREATED
        );
    }

    /**
     * Gets a collection of plots.
     *
     * @param userId The owner user identifier.
     * @param view The projection to fetch (e.g. 'overview').
     * @param includeCurrentImagery Whether to include current satellite imagery.
     * @return A collection of plot resources or a specific overview projection.
     */
    @GetMapping
    @Operation(
            summary = "Get plots collection",
            description = "Gets all active plots owned by a user. Use ?view=overview to get the My Plots overview projection, "
                    + "which returns registered plot totals, monitored area, climate links, online IoT devices and the latest monitoring signals per plot. "
                    + "If includeCurrentImagery is true, the response is enriched with current satellite imagery."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Plots retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PlotResource.class)))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    public ResponseEntity<?> getPlots(
            @Parameter(description = "The owner user identifier", required = true)
            @RequestParam Long userId,
            @Parameter(description = "Projection view. Supported values: 'overview'")
            @RequestParam(required = false) String view,
            @Parameter(description = "Include current satellite imagery data")
            @RequestParam(defaultValue = "false") boolean includeCurrentImagery
    ) {
        if ("overview".equalsIgnoreCase(view)) {
            var result = plotQueryService.handle(new GetMyPlotsOverviewQuery(userId));
            return ResponseEntityAssembler.toResponseEntityFromResult(
                    result,
                    MyPlotsOverviewResourceAssembler::toResourceFromReadModel,
                    HttpStatus.OK
            );
        }

        if (includeCurrentImagery) {
            var imageryResult = plotQueryService.handle(new GetPlotsWithCurrentImageryQuery(userId));
            return ResponseEntityAssembler.toResponseEntityFromResult(
                    imageryResult,
                    (java.util.List<com.arcadiadevs.viora.platform.agronomic.application.readmodels.PlotWithCurrentImagery> plots) -> plots.stream()
                            .map(PlotWithCurrentImageryResourceAssembler::toResourceFromReadModel)
                            .toList(),
                    HttpStatus.OK
            );
        }

        var result = plotQueryService.handle(new GetPlotsByUserIdQuery(userId));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                (java.util.List<com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot> plots) -> plots.stream()
                        .map(PlotResourceFromPlotAssembler::toResourceFromAggregate)
                        .toList(),
                HttpStatus.OK
        );
    }

    /**
     * Gets a single plot or its specific projections (detail, monitoring, weather).
     *
     * @param plotId The plot identifier.
     * @param view The specific projection to fetch (detail, monitoring, weather).
     * @param userId The user identifier, required for fetching complex projections.
     * @return The requested plot projection.
     */
    @GetMapping("/{plotId}")
    @Operation(
            summary = "Get plot by ID or specific projection",
            description = "Gets the details of a plot. Use ?view=detail for full configuration, "
                    + "?view=monitoring for the real-time agronomic summary, or ?view=weather for the 5-day forecast."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plot data retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid plot ID or missing userId for view"),
            @ApiResponse(responseCode = "404", description = "Plot not found")
    })
    public ResponseEntity<?> getPlot(
            @Parameter(description = "Plot identifier", required = true)
            @PathVariable Long plotId,
            @Parameter(description = "Projection view. Supported values: 'detail', 'monitoring', 'weather'")
            @RequestParam(required = false) String view,
            @Parameter(description = "User identifier (required when using a view projection)")
            @RequestParam(required = false) Long userId
    ) {
        if ("detail".equalsIgnoreCase(view)) {
            if (userId == null) return ResponseEntity.badRequest().body(new MessageResource("userId is required for detail view"));
            var result = plotDetailQueryService.handle(new GetPlotDetailQuery(userId, plotId));
            return ResponseEntityAssembler.toResponseEntityFromResult(
                    result,
                    PlotDetailResourceAssembler::toResourceFromReadModel,
                    HttpStatus.OK
            );
        } else if ("monitoring".equalsIgnoreCase(view)) {
            if (userId == null) return ResponseEntity.badRequest().body(new MessageResource("userId is required for monitoring view"));
            var result = plotMonitoringSummaryQueryService.handle(new GetPlotMonitoringSummaryQuery(userId, plotId));
            return ResponseEntityAssembler.toResponseEntityFromResult(
                    result,
                    PlotMonitoringSummaryResourceAssembler::toResourceFromReadModel,
                    HttpStatus.OK
            );
        } else if ("weather".equalsIgnoreCase(view)) {
            if (userId == null) return ResponseEntity.badRequest().body(new MessageResource("userId is required for weather view"));
            var result = plotWeatherForecastQueryService.handle(new GetPlotWeatherForecastQuery(userId, plotId));
            return ResponseEntityAssembler.toResponseEntityFromResult(
                    result,
                    PlotWeatherForecastResourceAssembler::toResourceFromReadModel,
                    HttpStatus.OK
            );
        }

        var result = plotQueryService.handle(new GetPlotByIdQuery(plotId));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                PlotResourceFromPlotAssembler::toResourceFromAggregate,
                HttpStatus.OK
        );
    }

    /**
     * Updates an existing plot.
     *
     * @param plotId The plot identifier.
     * @param userId The user identifier.
     * @param resource The plot update information, including optional chill requirement updates.
     * @return The updated plot resource.
     */
    @PatchMapping(value = "/{plotId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Update plot",
            description = "Updates the information and/or geographic boundary of an existing plot. "
                    + "If 'chillRequirement' is provided, it declares or overrides the winter-chill requirement. "
                    + "If 'clearChillRequirement' is true, it reverts to the crop-derived system default."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plot updated successfully", content = @Content(schema = @Schema(implementation = PlotResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Plot not found")
    })
    public ResponseEntity<?> updatePlot(
            @Parameter(description = "Plot identifier", required = true)
            @PathVariable Long plotId,
            @Parameter(description = "User identifier (required when modifying chill requirement)")
            @RequestParam(required = false) Long userId,
            @Valid @RequestBody UpdatePlotResource resource
    ) {
        if (resource.chillRequirement() != null && userId != null) {
            var command = ConfigureChillRequirementCommandFromResourceAssembler.toCommandFromResource(plotId, userId, resource.chillRequirement());
            var result = plotCommandService.handle(command);
            if (result.isFailure()) {
                return ResponseEntityAssembler.toResponseEntityFromResult(result, Object::toString, HttpStatus.OK);
            }
        } else if (Boolean.TRUE.equals(resource.clearChillRequirement()) && userId != null) {
            var command = new ResetChillRequirementCommand(plotId, userId);
            var result = plotCommandService.handle(command);
            if (result.isFailure()) {
                return ResponseEntityAssembler.toResponseEntityFromResult(result, Object::toString, HttpStatus.OK);
            }
        }

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
     * @return A success message if deleted.
     */
    @DeleteMapping("/{plotId}")
    @Operation(
            summary = "Delete plot",
            description = "Deletes an existing productive agricultural plot by its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plot deleted successfully", content = @Content(schema = @Schema(implementation = MessageResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid plot ID"),
            @ApiResponse(responseCode = "404", description = "Plot not found")
    })
    public ResponseEntity<?> deletePlot(
            @Parameter(description = "Plot identifier", required = true)
            @PathVariable Long plotId
    ) {
        var deletePlotCommand = new DeletePlotCommand(plotId);
        var result = plotCommandService.handle(deletePlotCommand);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                MessageResource::new,
                HttpStatus.OK
        );
    }
}
