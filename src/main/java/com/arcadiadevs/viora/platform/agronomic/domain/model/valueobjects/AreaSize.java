package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * AreaSize value object.
 *
 * <p>
 *     Represents the geodesic area enclosed by a plot boundary, in hectares.
 * </p>
 */
@Getter
@EqualsAndHashCode
public class AreaSize {
    private static final BigDecimal MAX_HECTARES = new BigDecimal("99999999.99");

    /**
     * The area size in hectares.
     */
    private final BigDecimal hectares;

    /**
     * Constructor for AreaSize.
     * @param hectares The area size in hectares.
     */
    public AreaSize(BigDecimal hectares) {
        if (hectares == null) {
            throw new IllegalArgumentException("Area size is required.");
        }

        if (hectares.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Area size must be greater than zero.");
        }

        if (hectares.compareTo(MAX_HECTARES) > 0) {
            throw new IllegalArgumentException("Area size cannot exceed 99999999.99 hectares.");
        }

        if (hectares.stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException("Area size cannot have more than 2 decimal places.");
        }

        this.hectares = hectares;
    }

    /**
     * Calculates the area enclosed by a validated polygon.
     *
     * @param polygonCoordinates The plot boundary.
     * @return The calculated area size.
     */
    public static AreaSize calculatedFrom(PolygonCoordinates polygonCoordinates) {
        if (polygonCoordinates == null) {
            throw new IllegalArgumentException("Polygon coordinates are required.");
        }
        return new AreaSize(polygonCoordinates.estimatedAreaHectares());
    }

    /**
     * Creates an AreaSize from a double value.
     * @param hectares The area size in hectares.
     * @return The AreaSize value object.
     */
    public static AreaSize fromHectares(Double hectares) {
        if (hectares == null) {
            throw new IllegalArgumentException("Area size is required.");
        }

        return new AreaSize(BigDecimal.valueOf(hectares));
    }
}
