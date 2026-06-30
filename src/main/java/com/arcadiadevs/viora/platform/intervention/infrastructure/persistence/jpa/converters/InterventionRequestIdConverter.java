package com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.converters;

import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.InterventionRequestId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class InterventionRequestIdConverter implements AttributeConverter<InterventionRequestId, Long> {

    @Override
    public Long convertToDatabaseColumn(InterventionRequestId attribute) {
        return attribute != null ? attribute.value() : null;
    }

    @Override
    public InterventionRequestId convertToEntityAttribute(Long dbData) {
        return dbData != null ? new InterventionRequestId(dbData) : null;
    }
}
