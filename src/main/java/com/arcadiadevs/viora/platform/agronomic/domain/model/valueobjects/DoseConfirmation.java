package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

/**
 * Confirms whether a nutrition application followed the recommended dosage.
 */
public enum DoseConfirmation {

    /** The inputs were applied at the recommended dosage. */
    AS_RECOMMENDED,

    /** The dosage was adjusted in field relative to the recommendation. */
    ADJUSTED;

    public static DoseConfirmation fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Dose confirmation is required.");
        }
        try {
            return DoseConfirmation.valueOf(value.trim().toUpperCase().replace("-", "_"));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Invalid dose confirmation '%s'. Allowed: AS_RECOMMENDED, ADJUSTED.".formatted(value));
        }
    }
}
