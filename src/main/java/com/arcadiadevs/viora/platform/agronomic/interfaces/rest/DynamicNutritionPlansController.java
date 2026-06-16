package com.arcadiadevs.viora.platform.agronomic.interfaces.rest;

import com.arcadiadevs.viora.platform.agronomic.application.commandservices.DynamicNutritionPlanCommandService;
import com.arcadiadevs.viora.platform.agronomic.application.queryservices.DynamicNutritionPlanQueryService;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetActiveDynamicNutritionPlanQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.RecommendDynamicNutritionCommand;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.CertifyNutritionApplicationResource;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.DynamicNutritionPlanResource;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform.CertifyNutritionApplicationCommandFromResourceAssembler;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform.DynamicNutritionPlanResourceFromDynamicNutritionPlanAssembler;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Dynamic nutrition plans REST controller.
 *
 * <p>
 * Exposes endpoints to recommend dynamic nutrition plans for a plot
 * and to retrieve the currently active plan.
 * </p>
 */
@RestController
@RequestMapping(value = "/api/v1/dynamic-nutrition-plans", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Dynamic Nutrition Plans", description = "Dynamic Nutrition Plan Management Endpoints")
public class DynamicNutritionPlansController {

    private final DynamicNutritionPlanCommandService dynamicNutritionPlanCommandService;
    private final DynamicNutritionPlanQueryService dynamicNutritionPlanQueryService;

    /**
     * Recommends a new dynamic nutrition plan for the specified plot.
     *
     * @param userId The owner user identifier.
     * @param plotId The plot identifier.
     * @return The recommended plan resource, or a standardized error response.
     */
    @PostMapping
    @Operation(
            summary = "Recommend dynamic nutrition plan",
            description = "Generates a dynamic nutrition plan for a plot from the latest usable "
                    + "AgroMonitoring NDVI imagery, current AgroMonitoring weather and evaluated climate risk. "
                    + "Any previously active plan for the plot is superseded."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Dynamic nutrition plan recommended",
                    content = @Content(schema = @Schema(implementation = DynamicNutritionPlanResource.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "403", description = "Authenticated user does not own the plot"),
            @ApiResponse(responseCode = "404", description = "Plot not found"),
            @ApiResponse(responseCode = "422", description = "Plan cannot be generated for the current plot condition"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    public ResponseEntity<?> recommendDynamicNutritionPlan(
            @Parameter(
                    description = "Temporary caller user identifier until IAM supplies the authenticated principal.",
                    required = true
            )
            @RequestParam Long userId,

            @Parameter(description = "Plot identifier", required = true)
            @RequestParam Long plotId
    ) {
        var command = new RecommendDynamicNutritionCommand(userId, plotId, null);
        var result = dynamicNutritionPlanCommandService.handle(command);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                DynamicNutritionPlanResourceFromDynamicNutritionPlanAssembler::toResourceFromAggregate,
                HttpStatus.CREATED
        );
    }

    /**
     * Retrieves the currently active dynamic nutrition plan for a plot.
     *
     * @param userId The owner user identifier.
     * @param plotId The plot identifier.
     * @return The active plan resource, or a standardized error response.
     */
    @GetMapping("/active")
    @Operation(
            summary = "Get active dynamic nutrition plan",
            description = "Retrieves the currently active dynamic nutrition plan for a plot, "
                    + "including its recommended inputs, application window and rationale."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Active dynamic nutrition plan retrieved",
                    content = @Content(schema = @Schema(implementation = DynamicNutritionPlanResource.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "403", description = "Authenticated user does not own the plot"),
            @ApiResponse(responseCode = "404", description = "No active plan found for the plot"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    public ResponseEntity<?> getActiveDynamicNutritionPlan(
            @Parameter(
                    description = "Temporary caller user identifier until IAM supplies the authenticated principal.",
                    required = true
            )
            @RequestParam Long userId,

            @Parameter(description = "Plot identifier", required = true)
            @RequestParam Long plotId
    ) {
        var query = new GetActiveDynamicNutritionPlanQuery(userId, plotId);
        var result = dynamicNutritionPlanQueryService.handle(query);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                DynamicNutritionPlanResourceFromDynamicNutritionPlanAssembler::toResourceFromAggregate,
                HttpStatus.OK
        );
    }

    /**
     * Certifies the in-field execution of a dynamic nutrition plan.
     *
     * @param planId The dynamic nutrition plan identifier.
     * @param userId The owner user identifier.
     * @param resource The certification request body.
     * @return The certified plan resource, or a standardized error response.
     */
    @PostMapping(value = "/{planId}/certification", consumes = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Certify nutrition plan application",
            description = "Registers the in-field execution of a dynamic nutrition plan (date, time, applied "
                    + "inputs, dose confirmation, operator and notes). The certification becomes part of the "
                    + "plot history and is published for downstream contexts such as expense declaration."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Application certified",
                    content = @Content(schema = @Schema(implementation = DynamicNutritionPlanResource.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "403", description = "Authenticated user does not own the plan"),
            @ApiResponse(responseCode = "404", description = "Dynamic nutrition plan not found"),
            @ApiResponse(responseCode = "422", description = "Plan cannot be certified in its current state"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    public ResponseEntity<?> certifyNutritionApplication(
            @PathVariable Long planId,

            @Parameter(
                    description = "Temporary caller user identifier until IAM supplies the authenticated principal.",
                    required = true
            )
            @RequestParam Long userId,

            @RequestBody CertifyNutritionApplicationResource resource
    ) {
        var command = CertifyNutritionApplicationCommandFromResourceAssembler.toCommandFromResource(
                userId,
                planId,
                resource
        );
        var result = dynamicNutritionPlanCommandService.handle(command);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                DynamicNutritionPlanResourceFromDynamicNutritionPlanAssembler::toResourceFromAggregate,
                HttpStatus.OK
        );
    }
}
