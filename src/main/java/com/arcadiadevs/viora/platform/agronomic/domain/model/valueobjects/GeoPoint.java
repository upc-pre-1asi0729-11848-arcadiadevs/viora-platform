package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * GeoPoint value object
 *
 * <p>
 *     Represent a geographic coordinate using latitude and longitude.
 * </p>
 */
@Getter
@EqualsAndHashCode
public class GeoPoint {

    /**
     * The latitude coordinate.
     */
    private final Double latitude;

    /**
     * The longitude coordinate.
     */
    private final Double longitude;

    /**
     * Constructor for GeoPoint.
     * @param latitude The latitude value.
     * @param longitude The longitude value.
     */
    public GeoPoint(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("Latitude and longitude are required.");
        }

        if (!Double.isFinite(latitude) || !Double.isFinite(longitude)) {
            throw new IllegalArgumentException("Latitude and longitude must be finite numbers.");
        }

        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90.");
        }

        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180.");
        }

        this.latitude = latitude;
        this.longitude = longitude;
    }
}
