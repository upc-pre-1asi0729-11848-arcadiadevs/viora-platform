package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import com.arcadiadevs.viora.platform.agronomic.domain.exceptions.InvalidAgronomicMetricException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * NDVI value object.
 */
@Getter
@EqualsAndHashCode
public class NdviValue {

    private final Double value;

    public NdviValue(Double value) {
        if (value == null) {
            throw new InvalidAgronomicMetricException("NDVI value is required.");
        }
        if (value < -1.0 || value > 1.0) {
            throw new InvalidAgronomicMetricException("NDVI value must be between -1.0 and 1.0.");
        }
        this.value = value;
    }
}