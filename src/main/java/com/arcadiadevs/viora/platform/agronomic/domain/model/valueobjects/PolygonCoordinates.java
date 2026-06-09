package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import com.arcadiadevs.viora.platform.agronomic.domain.exceptions.InvalidPolygonCoordinatesException;
import lombok.EqualsAndHashCode;

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