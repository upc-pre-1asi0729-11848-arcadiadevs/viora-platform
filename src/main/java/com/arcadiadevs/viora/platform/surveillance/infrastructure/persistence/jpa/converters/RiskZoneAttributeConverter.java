package com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.converters;

import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.RiskZone;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RiskZoneAttributeConverter implements AttributeConverter<RiskZone, String> {

    @Override
    public String convertToDatabaseColumn(RiskZone attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public RiskZone convertToEntityAttribute(String dbData) {
        return dbData == null ? null : RiskZone.valueOf(dbData);
    }
}
