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
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform.PlotDetailResourceAssembler;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform.PlotMonitoringSummaryResourceAssembler;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform.PlotResourceFromPlotAssembler;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform.PlotWeatherForecastResourceAssembler;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform.PlotRegistrationResourceAssembler;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform.PlotWithCurrentImageryResourceAssembler;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform.UpdatePlotCommandFromResourceAssembler;
import com.arcadiadevs.viora.platform.shared.interfaces.rest.resources.MessageResource;
import com.arcadiadevs.viora.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
     * Plot Detail query service.
     */
    private final PlotDetailQueryService plotDetailQueryService;

    /**
     * Per-plot monitoring summary query service.
     */
    private final PlotMonitoringSummaryQueryService plotMonitoringSummaryQueryService;

    /**
     * Per-plot weather forecast query service.
     */
    private final PlotWeatherForecastQueryService plotWeatherForecastQueryService;

    /**
     * Plot command service.
     */
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
            @ApiResponse(
                    responseCode = "201",
                    description = "Plot created",
                    content = @Content(schema = @Schema(
                            implementation = PlotRegistrationResource.class
                    ))
            ),
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
     * Gets the cards and per-plot monitoring rows for the My Plots screen.
     *
     * @param userId The owner user identifier.
     * @return The My Plots overview projection.
     */
    @GetMapping("/overview")
    @Operation(
            summary = "Get My Plots overview",
            description = "Returns registered plot totals, monitored area, climate links, "
                    + "online IoT devices and the latest monitoring signals per plot."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "My Plots overview retrieved",
                    content = @Content(schema = @Schema(
                            implementation = MyPlotsOverviewResource.class
                    ))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid user ID"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    public ResponseEntity<?> getMyPlotsOverview(@RequestParam Long userId) {
        var result = plotQueryService.handle(new GetMyPlotsOverviewQuery(userId));

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                MyPlotsOverviewResourceAssembler::toResourceFromReadModel,
                HttpStatus.OK
        );
    }

    /**
     * Gets the configuration and monitoring detail for one plot.
     *
     * @param plotId Plot identifier.
     * @param userId Owner user identifier.
     * @return Plot detail projection.
     */
    @GetMapping("/{plotId}/detail")
    @Operation(
            summary = "Get plot detail",
            description = "Returns plot configuration, boundary status, monitoring links, "
                    + "IoT activity and recent persisted configuration activity."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Plot detail retrieved",
                    content = @Content(schema = @Schema(
                            implementation = PlotDetailResource.class
                    ))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "403", description = "User does not own the plot"),
            @ApiResponse(responseCode = "404", description = "Plot not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    public ResponseEntity<?> getPlotDetail(
            @PathVariable Long plotId,
            @RequestParam Long userId
    ) {
        var result = plotDetailQueryService.handle(new GetPlotDetailQuery(userId, plotId));

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                PlotDetailResourceAssembler::toResourceFromReadModel,
                HttpStatus.OK
        );
    }

    /**
     * Gets the real-time monitoring summary for one plot.
     *
     * @param plotId Plot identifier.
     * @param userId Owner user identifier.
     * @return Per-plot monitoring summary projection.
     */
    @GetMapping("/{plotId}/monitoring-summary")
    @Operation(
            summary = "Get plot monitoring summary",
            description = "Returns the real-time monitoring summary for a single plot: current NDVI, "
                    + "NDVI trend, chill portions, consolidated health, phenological risk, weather and climate risk, "
                    + "last update, mitigation recommendations and the availability and freshness of "
                    + "each external data source."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Plot monitoring summary retrieved",
                    content = @Content(schema = @Schema(
                            implementation = PlotMonitoringSummaryResource.class
                    ))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "403", description = "User does not own the plot"),
            @ApiResponse(responseCode = "404", description = "Plot not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    public ResponseEntity<?> getPlotMonitoringSummary(
            @PathVariable Long plotId,
            @RequestParam Long userId
    ) {
        var result = plotMonitoringSummaryQueryService.handle(
                new GetPlotMonitoringSummaryQuery(userId, plotId)
        );

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                PlotMonitoringSummaryResourceAssembler::toResourceFromReadModel,
                HttpStatus.OK
        );
    }

    /**
     * Gets the weather forecast for one plot.
     *
     * @param plotId Plot identifier.
     * @param userId Owner user identifier.
     * @return Per-plot weather forecast projection.
     */
    @GetMapping("/{plotId}/weather-forecast")
    @Operation(
            summary = "Get plot weather forecast",
            description = "Returns the detailed weather forecast for a plot (about five days, the "
                    + "provider's available window): hourly readings, daily minimum/maximum, humidity, "
                    + "wind and gusts, precipitation, thermal anomaly and agronomic warnings, plus the "
                    + "availability and freshness of the weather source."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Weather forecast retrieved",
                    content = @Content(schema = @Schema(
                            implementation = PlotWeatherForecastResource.class
                    ))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "403", description = "User does not own the plot"),
            @ApiResponse(responseCode = "404", description = "Plot not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    public ResponseEntity<?> getPlotWeatherForecast(
            @PathVariable Long plotId,
            @RequestParam Long userId
    ) {
        var result = plotWeatherForecastQueryService.handle(
                new GetPlotWeatherForecastQuery(userId, plotId)
        );

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                PlotWeatherForecastResourceAssembler::toResourceFromReadModel,
                HttpStatus.OK
        );
    }

    /**
     * Declares the winter-chill requirement for a plot from its agronomic
     * configuration.
     *
     * @param plotId Plot identifier.
     * @param userId Owner user identifier.
     * @param resource The declared chill requirement.
     * @return The plot's effective chill requirement after the change.
     */
    @PutMapping(value = "/{plotId}/chill-requirement", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Configure plot chill requirement",
            description = "Declares the plot's winter-chill requirement (Dynamic Model chill portions), "
                    + "overriding the crop-derived system default. The stored value is tagged as "
                    + "USER_DECLARED provenance and shared by the trend chart's reference line and the "
                    + "yield-forecast chill modifier."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Chill requirement configured",
                    content = @Content(schema = @Schema(implementation = ChillRequirementResource.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "User does not own the plot"),
            @ApiResponse(responseCode = "404", description = "Plot not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    public ResponseEntity<?> configureChillRequirement(
            @PathVariable Long plotId,
            @RequestParam Long userId,
            @Valid @RequestBody ConfigureChillRequirementResource resource
    ) {
        var command = ConfigureChillRequirementCommandFromResourceAssembler
                .toCommandFromResource(plotId, userId, resource);
        var result = plotCommandService.handle(command);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                ChillRequirementResourceAssembler::toResourceFromValueObject,
                HttpStatus.OK
        );
    }

    /**
     * Clears a plot's declared chill requirement, reverting to the crop-derived
     * system default.
     *
     * @param plotId Plot identifier.
     * @param userId Owner user identifier.
     * @return The plot's effective (system-default) chill requirement.
     */
    @DeleteMapping("/{plotId}/chill-requirement")
    @Operation(
            summary = "Reset plot chill requirement",
            description = "Clears the declared chill requirement so the crop-derived system default "
                    + "applies again."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Chill requirement reset",
                    content = @Content(schema = @Schema(implementation = ChillRequirementResource.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "403", description = "User does not own the plot"),
            @ApiResponse(responseCode = "404", description = "Plot not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    public ResponseEntity<?> resetChillRequirement(
            @PathVariable Long plotId,
            @RequestParam Long userId
    ) {
        var result = plotCommandService.handle(new ResetChillRequirementCommand(plotId, userId));

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                ChillRequirementResourceAssembler::toResourceFromValueObject,
                HttpStatus.OK
        );
    }

    /**
     * Gets all active plots owned by a user.
     *
     * @param userId The owner user identifier.
     * @param includeCurrentImagery Whether current satellite imagery should be included.
     * @return The active plot resources.
     */
    @GetMapping
    @Operation(
            summary = "Get plots by user",
            description = "Gets all active plots owned by a user. When includeCurrentImagery is true, "
                    + "the response is enriched with current satellite imagery for the Viora dashboard."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Plots retrieved",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = PlotWithCurrentImageryResource.class
                                    )
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid user ID"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    public ResponseEntity<?> getPlotsByUserId(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "false") boolean includeCurrentImagery
    ) {
        if (includeCurrentImagery) {
            var imageryResult = plotQueryService.handle(
                    new GetPlotsWithCurrentImageryQuery(userId)
            );

            return ResponseEntityAssembler.toResponseEntityFromResult(
                    imageryResult,
                    plots -> plots.stream()
                            .map(PlotWithCurrentImageryResourceAssembler::toResourceFromReadModel)
                            .toList(),
                    HttpStatus.OK
            );
        }

        var result = plotQueryService.handle(new GetPlotsByUserIdQuery(userId));

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                plots -> plots.stream()
                        .map(PlotResourceFromPlotAssembler::toResourceFromAggregate)
                        .toList(),
                HttpStatus.OK
        );
    }

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
            description = "Updates the information and/or geographic boundary of an existing productive agricultural plot. "
                    + "When the boundary changes, the backend recalculates the plot area."
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
