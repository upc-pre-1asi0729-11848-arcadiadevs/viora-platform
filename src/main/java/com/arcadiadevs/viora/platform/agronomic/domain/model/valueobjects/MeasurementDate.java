package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import java.time.LocalDate;

/**
 * Value object representing the measurement date.
 *
 * <p>
 * This value object stores the date when an agronomic statistic was measured.
 * </p>
 *
 * @param measurementDate The measurement date. It cannot be null.
 */
public record MeasurementDate(LocalDate measurementDate) {

    /**
     * Compact constructor for MeasurementDate.
     * Validates that the measurementDate is not null.
     *
     * @throws IllegalArgumentException if the measurementDate is null.
     */
    public MeasurementDate {
        if (measurementDate == null) {
            throw new IllegalArgumentException("Measurement date cannot be null");
        }
    }
}