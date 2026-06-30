package com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.converters;

import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.PreHarvestInterval;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PreHarvestIntervalConverter implements AttributeConverter<PreHarvestInterval, Integer> {

    @Override
    public Integer convertToDatabaseColumn(PreHarvestInterval attribute) {
        return attribute == null ? null : attribute.days();
    }

    @Override
    public PreHarvestInterval convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : new PreHarvestInterval(dbData);
    }
}
