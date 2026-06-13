package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

import java.math.BigDecimal;
import java.util.List;

/**
 * Plot resource.
 *
 * <p>
 * Represents the plot data exposed through the REST API.
 * This resource is used by the frontend to display plot details.
 * </p>
 *
 * @param id The plot identifier.
 * @param userId The identifier of the grower who owns the plot.
 * @param name The plot name.
 * @param polygonCoordinates The geographic polygon coordinates.
 * @param areaSizeHectares The plot area size in hectares.
 * @param cropType The crop type.
 * @param variety The crop variety.
 * @param location The human-readable plot location.
 * @param campaign The production campaign.
 * @param notes Free-form grower notes.
 * @param state The plot activity state. Expected values: "enable" or "disable".
 */
public record PlotResource(
        Long id,
        Long userId,
        String name,
        List<List<Double>> polygonCoordinates,
        BigDecimal areaSizeHectares,
        String cropType,
        String variety,
        String location,
        String campaign,
        String notes,
        String state
) {
}