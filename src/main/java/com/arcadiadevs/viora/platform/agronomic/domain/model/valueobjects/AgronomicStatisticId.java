package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

/**
 * Value object representing the agronomic statistic id.
 *
 * <p>
 * This value object is used to identify an agronomic statistic.
 * It must be a positive Long value.
 * </p>
 *
 * @param agronomicStatisticId The agronomic statistic id. It cannot be null or less than 1.
 */
public record AgronomicStatisticId(Long agronomicStatisticId) {

    /**
     * Compact constructor for AgronomicStatisticId.
     * Validates that the agronomicStatisticId is not null and is greater than or equal to 1.
     *
     * @throws IllegalArgumentException if the agronomicStatisticId is null or less than 1.
     */
    public AgronomicStatisticId {
        if (agronomicStatisticId == null || agronomicStatisticId < 1) {
            throw new IllegalArgumentException("Agronomic statistic id cannot be null or less than 1");
        }
    }
}