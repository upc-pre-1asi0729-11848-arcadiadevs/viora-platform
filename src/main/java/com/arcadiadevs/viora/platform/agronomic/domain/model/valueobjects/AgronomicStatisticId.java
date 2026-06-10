package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Agronomic statistic identifier value object.
 */
@Getter
@EqualsAndHashCode
public class AgronomicStatisticId {

    private final Long value;

    public AgronomicStatisticId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Agronomic statistic ID must be a positive number.");
        }
        this.value = value;
    }
}