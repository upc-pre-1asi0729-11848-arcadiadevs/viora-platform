package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Plot list item shaped for the Viora dashboard and Mapbox adapter.
 *
 * @param id Plot identifier.
 * @param userId Owner user identifier.
 * @param name Plot name.
 * @param polygonCoordinates GeoJSON-compatible coordinates in [longitude, latitude] order.
 * @param areaSize Productive area in hectares.
 * @param lastUpdate Last satellite capture instant.
 * @param cropType Crop type.
 * @param variety Crop variety.
 * @param location Human-readable location.
 * @param campaign Production campaign.
 * @param notes Grower notes.
 * @param state Plot activity state.
 * @param healthStatus Current health classification when available.
 * @param phenologicalRisk Current phenological risk when available.
 * @param currentImagery Latest satellite imagery when available.
 */
public record PlotWithCurrentImageryResource(
        Long id,
        Long userId,
        String name,
        List<List<Double>> polygonCoordinates,
        BigDecimal areaSize,
        Instant lastUpdate,
        String cropType,
        String variety,
        String location,
        String campaign,
        String notes,
        String state,
        String healthStatus,
        String phenologicalRisk,
        SatelliteImageryResource currentImagery
) {
}
