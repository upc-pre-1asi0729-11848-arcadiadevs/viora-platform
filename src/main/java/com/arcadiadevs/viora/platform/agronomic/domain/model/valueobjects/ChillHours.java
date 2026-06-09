package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import java.math.BigDecimal;

/**
 * Value object representing chill hours.
 *
 * <p>
 * Chill hours are climatic metrics used to evaluate cold accumulation.
 * They cannot be negative.
 * </p>
 *
 * @param chillHours The chill hours value. It cannot be null or negative.
 */
public record ChillHours(BigDecimal chillHours) {

    /**
     * Compact constructor for ChillHours.
     * Validates that chillHours is not null or negative.
     *
     * @throws IllegalArgumentException if chillHours is null or negative.
     */
    public ChillHours {
        if (chillHours == null) {
            throw new IllegalArgumentException("Chill hours cannot be null");
        }

        if (chillHours.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Chill hours cannot be negative");
        }
    }
}