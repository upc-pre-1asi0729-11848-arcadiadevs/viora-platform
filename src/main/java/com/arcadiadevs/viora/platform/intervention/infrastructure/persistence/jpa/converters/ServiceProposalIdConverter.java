package com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.converters;

import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ServiceProposalId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ServiceProposalIdConverter implements AttributeConverter<ServiceProposalId, Long> {

    @Override
    public Long convertToDatabaseColumn(ServiceProposalId attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public ServiceProposalId convertToEntityAttribute(Long dbData) {
        return dbData == null ? null : new ServiceProposalId(dbData);
    }
}
