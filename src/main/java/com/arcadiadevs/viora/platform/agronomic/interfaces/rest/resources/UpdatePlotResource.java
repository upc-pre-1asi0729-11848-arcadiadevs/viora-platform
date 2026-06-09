package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

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
 */
public record UpdatePlotResource(
        @Size(min = 3, max = 80) String name,
        @Size(min = 4)
        List<@Size(min = 2, max = 2) List<Double>> polygonCoordinates,
        @Positive @DecimalMax("99999999.99") @Digits(integer = 8, fraction = 2)
        BigDecimal areaSizeHectares,
        @Size(max = 60) String cropType,
        @Size(max = 80) String variety
) {
}
