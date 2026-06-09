package com.arcadiadevs.viora.platform.agronomic.domain.exceptions;

/**
 * Exception thrown when polygon coordinates are invalid.
 *
 * @summary
 * This exception is thrown when a plot polygon is empty, incomplete or not closed.
 * @see RuntimeException
 */
public class InvalidPolygonCoordinatesException extends RuntimeException {
    /**
     * Constructor for the exception.
     * @param message The validation message.
     */
    public InvalidPolygonCoordinatesException(String message) {
        super(message);
    }
}