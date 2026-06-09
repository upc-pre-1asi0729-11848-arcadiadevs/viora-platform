package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

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
        String name,
        List<List<Double>> polygonCoordinates,
        BigDecimal areaSizeHectares,
        String cropType,
        String variety
) {
}