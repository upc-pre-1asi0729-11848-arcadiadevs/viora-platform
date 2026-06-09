package com.arcadiadevs.viora.platform.agronomic.domain.exceptions;

/**
 * Exception thrown when a requested plot does not exist.
 */
public class PlotNotFoundException extends RuntimeException {

    /**
     * Creates the exception for the plot that was not found.
     *
     * @param plotId The plot identifier.
     */
    public PlotNotFoundException(Long plotId) {
        super("Plot with ID %s was not found.".formatted(plotId));
    }
}
