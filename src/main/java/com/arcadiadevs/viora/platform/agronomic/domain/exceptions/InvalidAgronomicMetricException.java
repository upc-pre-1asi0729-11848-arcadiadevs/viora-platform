package com.arcadiadevs.viora.platform.agronomic.domain.exceptions;

/**
 * Exception thrown when an agronomic metric violates domain rules.
 */
public class InvalidAgronomicMetricException extends RuntimeException {

    public InvalidAgronomicMetricException(String message) {
        super(message);
    }
}