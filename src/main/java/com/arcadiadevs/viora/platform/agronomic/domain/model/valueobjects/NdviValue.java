package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import java.math.BigDecimal;

/**
 * Value object representing the NDVI value.
 *
 * <p>
 * NDVI is used to measure vegetation health.
 * Its value must be between -1 and 1.
 * </p>
 *
 * @param ndviValue The NDVI value. It cannot be null and must be between -1 and 1.
 */
public record NdviValue(BigDecimal ndviValue) {

    /**
     * Compact constructor for NdviValue.
     * Validates that the NDVI value is between -1 and 1.
     *
     * @throws IllegalArgumentException if the ndviValue is null or outside the valid range.
     */
    public NdviValue {
        if (ndviValue == null) {
            throw new IllegalArgumentException("NDVI value cannot be null");
        }

        if (ndviValue.compareTo(BigDecimal.valueOf(-1)) < 0 || ndviValue.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("NDVI value must be between -1 and 1");
        }
    }
}