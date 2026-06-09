package com.arcadiadevs.viora.platform.agronomic.domain.exceptions;

/**
 * Exception thrown when a plot cannot be deleted.
 *
 * <p>
 * This exception represents a domain conflict caused by operational records
 * that prevent the physical deletion of a plot.
 * </p>
 */
public class PlotDeletionConflictException extends RuntimeException {

    /**
     * Creates the exception for the plot that cannot be deleted.
     *
     * @param plotId The plot identifier.
     */
    public PlotDeletionConflictException(Long plotId) {
        super("Plot with ID %s cannot be deleted because it has related operational records."
                .formatted(plotId));
    }
}
