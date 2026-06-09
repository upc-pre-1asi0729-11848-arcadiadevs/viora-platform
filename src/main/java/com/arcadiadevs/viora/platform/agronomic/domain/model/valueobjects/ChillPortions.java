package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import java.math.BigDecimal;

/**
 * Value object representing chill portions.
 *
 * <p>
 * Chill portions are climatic metrics used to evaluate cold accumulation.
 * They cannot be negative.
 * </p>
 *
 * @param chillPortions The chill portions value. It cannot be null or negative.
 */
public record ChillPortions(BigDecimal chillPortions) {

    /**
     * Compact constructor for ChillPortions.
     * Validates that chillPortions is not null or negative.
     *
     * @throws IllegalArgumentException if chillPortions is null or negative.
     */
    public ChillPortions {
        if (chillPortions == null) {
            throw new IllegalArgumentException("Chill portions cannot be null");
        }

        if (chillPortions.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Chill portions cannot be negative");
        }
    }
}