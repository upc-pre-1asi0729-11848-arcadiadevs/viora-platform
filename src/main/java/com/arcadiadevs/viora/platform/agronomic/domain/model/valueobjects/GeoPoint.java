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

    /**
     * Computes the great-circle distance to another point using the Haversine
     * formula, in kilometers.
     *
     * @param other The other geographic point.
     * @return The distance between the two points in kilometers.
     */
    public double haversineKilometers(GeoPoint other) {
        final double earthRadiusKm = 6_371.0088;

        double deltaLatitude = Math.toRadians(other.latitude - this.latitude);
        double deltaLongitude = Math.toRadians(other.longitude - this.longitude);
        double originLatitude = Math.toRadians(this.latitude);
        double targetLatitude = Math.toRadians(other.latitude);

        double a = Math.sin(deltaLatitude / 2) * Math.sin(deltaLatitude / 2)
                + Math.sin(deltaLongitude / 2) * Math.sin(deltaLongitude / 2)
                * Math.cos(originLatitude) * Math.cos(targetLatitude);

        return earthRadiusKm * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
