package com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.converters;

import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.AlertSeverity;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AlertSeverityAttributeConverter implements AttributeConverter<AlertSeverity, String> {

    @Override
    public String convertToDatabaseColumn(AlertSeverity attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public AlertSeverity convertToEntityAttribute(String dbData) {
        return dbData == null ? null : AlertSeverity.valueOf(dbData);
    }
}
