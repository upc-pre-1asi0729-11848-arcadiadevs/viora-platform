package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import com.arcadiadevs.viora.platform.agronomic.domain.exceptions.InvalidAgronomicMetricException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Yield forecast value object.
 */
@Getter
@EqualsAndHashCode
public class YieldForecast {

    private final Double value;

    public YieldForecast(Double value) {
        if (value == null) {
            throw new InvalidAgronomicMetricException("Yield forecast value is required.");
        }
        if (value < 0) {
            throw new InvalidAgronomicMetricException("Yield forecast value cannot be negative.");
        }
        this.value = value;
    }
}