package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * UpdatePlot resource.
 *
 * <p>
 *     Represents the request body used by the frontend to update a plot.
 *     All fields are optional because the endpoint uses PATCH semantics.
 * </p>
 *
 * @param name The updated plot name.
 * @param polygonCoordinates The updated polygon coordinates.
 * @param cropType The updated crop type.
 * @param variety The updated crop variety.
 * @param location The updated plot location.
 * @param campaign The updated production campaign.
 * @param notes The updated grower notes.
 */
public record UpdatePlotResource(
        @Size(min = 3, max = 80) String name,
        @Size(min = 4)
        @Schema(
                description = "Closed polygon in GeoJSON [longitude, latitude] order. "
                        + "When supplied, the backend recalculates the plot area."
        )
        List<@Size(min = 2, max = 2) List<Double>> polygonCoordinates,
        @Size(max = 60) String cropType,
        @Size(max = 80) String variety,
        @Size(max = 120) String location,
        @Size(max = 60) String campaign,
        @Size(max = 500) String notes,
        
        @Schema(description = "Set to true to explicitly remove the user-declared chill requirement.")
        Boolean clearChillRequirement,
        
        @Schema(description = "The declared chill requirement. Setting this will override any existing requirement.")
        ConfigureChillRequirementResource chillRequirement
) {
}
