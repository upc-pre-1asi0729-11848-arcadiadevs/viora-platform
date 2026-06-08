package com.arcadiadevs.viora.platform.agronomic.domain.exceptions;

/**
 * Exeption thrown when a plot is not found
 *
 * @summary
 * This Exception is thrown when a request plot does not exist in the system.
 * @see RuntimeException
 */
public class PlotNotFoundException extends RuntimeException {

    /**
     * Constructor for the exception.
     * @param plotId the ID of the plot that was not found
     */
    public PlotNotFoundException(long plotId) {
        super(String.format("Plot with ID %s not found.", plotId));
    }
}
