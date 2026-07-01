package com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects;

/**
 * Availability window of a specialist, used to rank candidates and drive the
 * availability badges on the Expert Assistance screen.
 */
public enum SpecialistAvailability {
    AVAILABLE_TODAY,
    AVAILABLE_TOMORROW,
    AVAILABLE_THIS_WEEK,
    UNAVAILABLE;

    /** Whether the specialist can currently take a case. */
    public boolean isAvailable() {
        return this != UNAVAILABLE;
    }
}
