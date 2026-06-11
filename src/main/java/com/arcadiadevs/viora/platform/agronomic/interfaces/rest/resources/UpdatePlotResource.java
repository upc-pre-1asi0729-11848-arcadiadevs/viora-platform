package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
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
 * @param areaSizeHectares The updated area size in hectares.
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
                description = "Closed polygon in GeoJSON [longitude, latitude] order."
        )
        List<@Size(min = 2, max = 2) List<Double>> polygonCoordinates,
        @Positive @DecimalMax("99999999.99") @Digits(integer = 8, fraction = 2)
        BigDecimal areaSizeHectares,
        @Size(max = 60) String cropType,
        @Size(max = 80) String variety,
        @Size(max = 120) String location,
        @Size(max = 60) String campaign,
        @Size(max = 500) String notes
) {
}
