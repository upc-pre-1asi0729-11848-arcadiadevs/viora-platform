package com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.converters;

import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.ThreatType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ThreatTypeAttributeConverter implements AttributeConverter<ThreatType, String> {

    @Override
    public String convertToDatabaseColumn(ThreatType attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public ThreatType convertToEntityAttribute(String dbData) {
        return dbData == null ? null : ThreatType.valueOf(dbData);
    }
}
