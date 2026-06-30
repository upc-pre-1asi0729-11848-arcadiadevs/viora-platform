package com.arcadiadevs.viora.platform.intervention.domain.exceptions;

/**
 * Exception thrown when an intervention request is not found.
 */
public class InterventionRequestNotFoundException extends RuntimeException {
    public InterventionRequestNotFoundException(Long id) {
        super("Intervention Request with ID " + id + " not found.");
    }
}
