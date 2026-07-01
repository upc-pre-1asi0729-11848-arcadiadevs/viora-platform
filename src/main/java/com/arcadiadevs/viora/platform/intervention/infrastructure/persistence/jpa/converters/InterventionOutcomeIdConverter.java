package com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.converters;

import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.InterventionOutcomeId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class InterventionOutcomeIdConverter implements AttributeConverter<InterventionOutcomeId, Long> {

    @Override
    public Long convertToDatabaseColumn(InterventionOutcomeId attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public InterventionOutcomeId convertToEntityAttribute(Long dbData) {
        return dbData == null ? null : new InterventionOutcomeId(dbData);
    }
}
