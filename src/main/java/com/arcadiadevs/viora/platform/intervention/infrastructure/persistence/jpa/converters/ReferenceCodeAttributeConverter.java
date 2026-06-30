package com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.converters;

import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ReferenceCode;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link ReferenceCode} domain value object to/from String for JPA.
 */
@Converter(autoApply = true)
public class ReferenceCodeAttributeConverter implements AttributeConverter<ReferenceCode, String> {

    @Override
    public String convertToDatabaseColumn(ReferenceCode attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.code();
    }

    @Override
    public ReferenceCode convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        return new ReferenceCode(dbData);
    }
}
