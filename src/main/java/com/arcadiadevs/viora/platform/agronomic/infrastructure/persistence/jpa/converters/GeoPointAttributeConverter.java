package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.converters;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * GeoPoint JPA attribute converter.
 *
 * <p>
 * Converts a GeoPoint value object into a text representation and vice versa.
 * The stored format is: latitude,longitude
 * </p>
 */
@Converter
public class GeoPointAttributeConverter implements AttributeConverter<GeoPoint, String> {
    /**
     * Converts a GeoPoint value object into a database column value.
     * @param attribute The GeoPoint value object.
     * @return The text representation.
     */
    @Override
    public String convertToDatabaseColumn(GeoPoint attribute) {
        if (attribute == null) return null;
        return attribute.getLatitude() + "," + attribute.getLongitude();
    }

    /**
     * Converts a database column value into a GeoPoint value object.
     * @param dbData The text representation.
     * @return The GeoPoint value object.
     */
    @Override
    public GeoPoint convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return null;

        String[] values = dbData.split(",");

        if (values.length != 2) {
            throw new IllegalArgumentException("Invalid GeoPoint database format.");
        }

        return new GeoPoint(
                Double.parseDouble(values[0]),
                Double.parseDouble(values[1])
        );
    }
}