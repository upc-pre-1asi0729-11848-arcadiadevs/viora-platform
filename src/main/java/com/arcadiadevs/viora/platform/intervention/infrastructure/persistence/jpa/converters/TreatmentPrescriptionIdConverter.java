package com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.converters;

import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.TreatmentPrescriptionId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TreatmentPrescriptionIdConverter implements AttributeConverter<TreatmentPrescriptionId, Long> {

    @Override
    public Long convertToDatabaseColumn(TreatmentPrescriptionId attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public TreatmentPrescriptionId convertToEntityAttribute(Long dbData) {
        return dbData == null ? null : new TreatmentPrescriptionId(dbData);
    }
}
