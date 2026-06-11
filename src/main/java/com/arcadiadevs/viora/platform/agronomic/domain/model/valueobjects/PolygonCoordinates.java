package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import com.arcadiadevs.viora.platform.agronomic.domain.exceptions.InvalidPolygonCoordinatesException;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * PolygonCoordinates value object.
 *
 * <p>
 *     Represents the closed geographic polygon that delimits a plot.
 *     A valid polygon must have at least three different vertices and must be closed.
 * </p>
 */
@EqualsAndHashCode
public class PolygonCoordinates {

    /**
     * The polygon points.
     */
    private final List<GeoPoint> points;

    /**
     * Constructor for PolygonCoordinates.
     * @param points The list of geographic points.
     */
    public PolygonCoordinates(List<GeoPoint> points) {
        validatePolygon(points);
        this.points = new ArrayList<>(points);
    }

    /**
     * Gets the polygon points as an unmodifiable list.
     * @return The polygon points.
     */
    public List<GeoPoint> getPoints() {
        return Collections.unmodifiableList(points);
    }

    /**
     * Estimates the enclosed geodesic area of the polygon in hectares.
     *
     * <p>
     *     Uses the Chamberlain-Duquette spherical excess approximation (the same
     *     formula used by common web-map libraries), which is accurate for
     *     field-scale polygons. The result is rounded to two decimals.
     * </p>
     *
     * @return The estimated area in hectares.
     */
    public BigDecimal estimatedAreaHectares() {
        final double earthRadiusMeters = 6_371_008.8;
        double sum = 0.0;

        for (int i = 0; i < points.size() - 1; i++) {
            var current = points.get(i);
            var next = points.get(i + 1);
            sum += Math.toRadians(next.getLongitude() - current.getLongitude())
                    * (2 + Math.sin(Math.toRadians(current.getLatitude()))
                    + Math.sin(Math.toRadians(next.getLatitude())));
        }

        double squareMeters = Math.abs(sum * earthRadiusMeters * earthRadiusMeters / 2.0);
        return BigDecimal.valueOf(squareMeters / 10_000.0).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Computes a representative interior point of the plot as the arithmetic
     * mean of its distinct vertices (the closing vertex is excluded).
     *
     * <p>
     *     Used for location-based lookups such as weather, where a single
     *     latitude/longitude is required for the whole plot.
     * </p>
     *
     * @return The representative geographic point of the polygon.
     */
    public GeoPoint centroid() {
        List<GeoPoint> vertices = points.subList(0, points.size() - 1);
        double latitude = vertices.stream().mapToDouble(GeoPoint::getLatitude).average().orElseThrow();
        double longitude = vertices.stream().mapToDouble(GeoPoint::getLongitude).average().orElseThrow();
        return new GeoPoint(latitude, longitude);
    }

    /**
     * Validates the polygon coordinates.
     * @param points The points to validate.
     */
    private void validatePolygon(List<GeoPoint> points) {
        if (points == null || points.isEmpty()) {
            throw new InvalidPolygonCoordinatesException("Polygon coordinates are required.");
        }

        if (points.stream().anyMatch(Objects::isNull)) {
            throw new InvalidPolygonCoordinatesException("Polygon coordinates cannot contain null points.");
        }

        if (points.size() < 4) {
            throw new InvalidPolygonCoordinatesException("A closed polygon must have at least 4 points.");
        }

        GeoPoint firstPoint = points.getFirst();
        GeoPoint lastPoint = points.getLast();

        if (!firstPoint.equals(lastPoint)) {
            throw new InvalidPolygonCoordinatesException("The polygon must be closed. The first and last point must be equal.");
        }

        long uniqueVertices = points.subList(0, points.size() - 1)
                .stream()
                .distinct()
                .count();

        if (uniqueVertices < 3) {
            throw new InvalidPolygonCoordinatesException("A polygon must have at least 3 different vertices.");
        }
    }
}
