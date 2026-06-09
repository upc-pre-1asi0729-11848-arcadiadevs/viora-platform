package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.converters;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PolygonCoordinates;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.List;

/**
 * PolygonCoordinates JPA attribute converter.
 *
 * <p>
 * Converts PolygonCoordinates into a text representation and vice versa.
 * The stored format is: latitude,longitude;latitude,longitude;latitude,longitude
 * </p>
 */
@Converter
public class PolygonCoordinatesAttributeConverter implements AttributeConverter<PolygonCoordinates, String> {
    /**
     * Converts PolygonCoordinates into a database column value.
     * @param attribute The polygon coordinates.
     * @return The text representation.
     */
    @Override
    public String convertToDatabaseColumn(PolygonCoordinates attribute) {
        if (attribute == null) return null;

        return attribute.getPoints()
                .stream()
                .map(point -> point.getLatitude() + "," + point.getLongitude())
                .reduce((current, next) -> current + ";" + next)
                .orElse(null);
    }

    /**
     * Converts a database column value into PolygonCoordinates.
     * @param dbData The text representation.
     * @return The polygon coordinates value object.
     */
    @Override
    public PolygonCoordinates convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return null;

        List<GeoPoint> points = Arrays.stream(dbData.split(";"))
                .map(this::toGeoPoint)
                .toList();

        return new PolygonCoordinates(points);
    }

    /**
     * Converts a text point into a GeoPoint.
     * @param rawPoint The raw point text.
     * @return The GeoPoint value object.
     */
    private GeoPoint toGeoPoint(String rawPoint) {
        String[] values = rawPoint.split(",");

        if (values.length != 2) {
            throw new IllegalArgumentException("Invalid polygon point database format.");
        }

        return new GeoPoint(
                Double.parseDouble(values[0]),
                Double.parseDouble(values[1])
        );
    }
}