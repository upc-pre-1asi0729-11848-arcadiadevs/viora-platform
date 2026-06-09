package com.arcadiadevs.viora.platform.agronomic.domain.model.commands;

/**
 * DeletePlot command.
 *
 * <p>
 *     Represents the intention of deleting a productive agricultural plot.
 * </p>
 *
 * @param plotId The identifier of the plot to delete.
 */
public record DeletePlotCommand(Long plotId) {

    /**
     * Compact constructor for DeletePlotCommand.
     */
    public DeletePlotCommand {
        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException("Plot ID must be a positive number.");
        }
    }
}