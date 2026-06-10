package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import com.arcadiadevs.viora.platform.agronomic.domain.exceptions.InvalidAgronomicMetricException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Accumulated chill hours value object.
 */
@Getter
@EqualsAndHashCode
public class AccumulatedChillHours {

    private final Double value;

    public AccumulatedChillHours(Double value) {
        if (value == null) {
            throw new InvalidAgronomicMetricException("Chill hours value is required.");
        }
        if (value < 0) {
            throw new InvalidAgronomicMetricException("Chill hours cannot be negative.");
        }
        this.value = value;
    }
}