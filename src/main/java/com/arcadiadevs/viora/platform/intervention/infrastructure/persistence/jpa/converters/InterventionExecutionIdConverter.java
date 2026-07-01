package com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.converters;

import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.InterventionExecutionId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class InterventionExecutionIdConverter implements AttributeConverter<InterventionExecutionId, Long> {

    @Override
    public Long convertToDatabaseColumn(InterventionExecutionId attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public InterventionExecutionId convertToEntityAttribute(Long dbData) {
        return dbData == null ? null : new InterventionExecutionId(dbData);
    }
}
