package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import com.arcadiadevs.viora.platform.agronomic.domain.exceptions.InvalidAgronomicMetricException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Chill portions value object.
 */
@Getter
@EqualsAndHashCode
public class ChillPortions {

    private final Double value;

    public ChillPortions(Double value) {
        if (value == null) {
            throw new InvalidAgronomicMetricException("Chill portions value is required.");
        }
        if (value < 0) {
            throw new InvalidAgronomicMetricException("Chill portions cannot be negative.");
        }
        this.value = value;
    }
}