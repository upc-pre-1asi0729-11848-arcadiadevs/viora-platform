package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.converters;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts GeoPoint values to and from their persistence representation.
 */
@Converter
public class GeoPointAttributeConverter implements AttributeConverter<GeoPoint, String> {

    /**
     * Converts a geographic point into latitude,longitude format.
     *
     * @param attribute The geographic point.
     * @return The database representation.
     */
    @Override
    public String convertToDatabaseColumn(GeoPoint attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getLatitude() + "," + attribute.getLongitude();
    }

    /**
     * Reconstructs a geographic point from latitude,longitude format.
     *
     * @param dbData The database representation.
     * @return The geographic point.
     */
    @Override
    public GeoPoint convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }

        var values = dbData.split(",");
        if (values.length != 2) {
            throw new IllegalArgumentException("Invalid GeoPoint database format.");
        }

        return new GeoPoint(
                Double.parseDouble(values[0]),
                Double.parseDouble(values[1])
        );
    }
}
