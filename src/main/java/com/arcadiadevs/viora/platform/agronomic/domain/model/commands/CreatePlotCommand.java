package com.arcadiadevs.viora.platform.agronomic.domain.model.commands;

import java.math.BigDecimal;
import java.util.List;

/**
 * Command to create a Plot.
 *
 * <p>
 * Represents the intention of creating a new productive agricultural plot.
 * All core identity and geographic fields are strictly required.
 * </p>
 *
 * @param userId             The identifier of the owner user. Cannot be null or non-positive.
 * @param name               The business name assigned to the plot. Cannot be null or blank.
 * @param polygonCoordinates The geographic polygon coordinates that delimit the plot. Cannot be null or empty.
 * @param areaSizeHectares   The productive area size in hectares. Cannot be null or negative.
 * @param cropType           The crop type associated with the plot. Can be null or blank (optional).
 * @param variety            The crop variety associated with the plot. Can be null or blank (optional).
 */
public record CreatePlotCommand(
        Long userId,
        String name,
        List<List<Double>> polygonCoordinates,
        BigDecimal areaSizeHectares,
        String cropType,
        String variety
) {
    /**
     * Compact constructor for CreatePlotCommand.
     * Validates that all mandatory fields are present and structurally valid.
     */
    public CreatePlotCommand {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Plot name cannot be null or blank.");
        }
        if (polygonCoordinates == null || polygonCoordinates.isEmpty()) {
            throw new IllegalArgumentException("Polygon coordinates cannot be null or empty.");
        }
        if (areaSizeHectares == null || areaSizeHectares.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Area size in hectares cannot be null or negative.");
        }
    }
}