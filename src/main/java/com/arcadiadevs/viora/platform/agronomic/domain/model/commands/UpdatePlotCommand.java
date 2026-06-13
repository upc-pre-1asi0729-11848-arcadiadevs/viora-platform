package com.arcadiadevs.viora.platform.agronomic.domain.model.commands;

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
 * @param cropType The new crop type.
 * @param variety The new crop variety.
 * @param location The new plot location.
 * @param campaign The new production campaign.
 * @param notes The new grower notes.
 */
public record UpdatePlotCommand(
        Long plotId,
        String name,
        List<List<Double>> polygonCoordinates,
        String cropType,
        String variety,
        String location,
        String campaign,
        String notes
) {
    /**
     * Backwards-compatible constructor for updates without descriptive metadata.
     */
    public UpdatePlotCommand(
            Long plotId,
            String name,
            List<List<Double>> polygonCoordinates,
            String cropType,
            String variety
    ) {
        this(
                plotId,
                name,
                polygonCoordinates,
                cropType,
                variety,
                null,
                null,
                null
        );
    }

    /**
     * Compact constructor for UpdatePlotCommand.
     */
    public UpdatePlotCommand {
        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException("Plot ID must be a positive number.");
        }

        if (name == null
                && polygonCoordinates == null
                && cropType == null
                && variety == null
                && location == null
                && campaign == null
                && notes == null) {
            throw new IllegalArgumentException("At least one plot field must be provided.");
        }
    }
}
