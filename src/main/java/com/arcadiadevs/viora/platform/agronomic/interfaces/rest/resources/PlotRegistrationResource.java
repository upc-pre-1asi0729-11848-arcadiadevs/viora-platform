package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

import java.math.BigDecimal;
import java.util.List;

/**
 * Plot registration response used by the creation confirmation screen.
 *
 * @param id Plot identifier.
 * @param userId Owner user identifier.
 * @param name Plot name.
 * @param polygonCoordinates Boundary coordinates in GeoJSON order.
 * @param areaSizeHectares Area calculated by the backend from the geographic boundary.
 * @param cropType Crop type.
 * @param variety Crop variety.
 * @param location Human-readable location.
 * @param campaign Production campaign.
 * @param notes Grower notes.
 * @param state Plot activity state.
 * @param climateMonitoring Initial climate integration state.
 * @param satelliteNdvi Initial satellite NDVI integration state.
 * @param iotDevices Initial IoT integration state.
 */
public record PlotRegistrationResource(
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
        String state,
        String climateMonitoring,
        String satelliteNdvi,
        String iotDevices
) {
}
