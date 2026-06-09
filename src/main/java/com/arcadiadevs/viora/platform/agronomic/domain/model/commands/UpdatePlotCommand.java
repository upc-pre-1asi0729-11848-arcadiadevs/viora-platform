package com.arcadiadevs.viora.platform.agronomic.domain.model.commands;

import java.math.BigDecimal;
import java.util.List;

/**
 * UpdatePlot command.
 *
 * <p>
 *     Represents the intention of updating an existing productive agricultural plot.
 *     Since the operation is exposed through PATCH, fields can be partially provided.
 * </p>
 *
 * @param plotId The identifier of the plot to update.
 * @param name The new plot name.
 * @param polygonCoordinates The new geographic polygon coordinates.
 * @param areaSizeHectares The new area size in hectares.
 * @param cropType The new crop type.
 * @param variety The new crop variety.
 */
public record UpdatePlotCommand(
        Long plotId,
        String name,
        List<List<Double>> polygonCoordinates,
        BigDecimal areaSizeHectares,
        String cropType,
        String variety
) {
    /**
     * Compact constructor for UpdatePlotCommand.
     */
    public UpdatePlotCommand {
        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException("Plot ID must be a positive number.");
        }

        if (name == null
                && polygonCoordinates == null
                && areaSizeHectares == null
                && cropType == null
                && variety == null) {
            throw new IllegalArgumentException("At least one plot field must be provided.");
        }
    }
}
