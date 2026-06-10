package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;

/**
 * Measurement date value object.
 */
@Getter
@EqualsAndHashCode
public class MeasurementDate {

    private final LocalDate value;

    public MeasurementDate(LocalDate value) {
        if (value == null) {
            throw new IllegalArgumentException("Measurement date is required.");
        }
        this.value = value;
    }
}