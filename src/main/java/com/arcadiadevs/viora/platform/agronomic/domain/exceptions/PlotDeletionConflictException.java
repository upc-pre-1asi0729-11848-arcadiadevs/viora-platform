package com.arcadiadevs.viora.platform.agronomic.domain.exceptions;

/**
 * Exception thrown when a plot cannot be deleted.
 *
 * @summary
 * This exception is thrown when a plot has related operational records
 * that prevent a direct deletion.
 * @see RuntimeException
 */
public class PlotDeletionConflictException extends RuntimeException {
    /**
     * Constructor for the exception.
     * @param plotId The ID of the plot that cannot be deleted.
     */
    public PlotDeletionConflictException(Long plotId) {
        super(String.format("Plot with ID %s cannot be deleted because it has related operational records.", plotId));
    }
}