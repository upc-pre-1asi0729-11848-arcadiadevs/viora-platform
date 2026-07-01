package com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.assemblers;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.Specialist;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.SpecialistId;
import com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.entities.SpecialistEntity;

import java.util.List;

public class SpecialistEntityAssembler {

    public static SpecialistEntity toEntity(Specialist domain) {
        var entity = new SpecialistEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId().value());
        }
        entity.setFullName(domain.getFullName());
        entity.setRole(domain.getRole());
        entity.setSuccessRate(domain.getSuccessRate());
        entity.setCaseCount(domain.getCaseCount());
        entity.setDistanceKm(domain.getDistanceKm());
        entity.setTags(domain.getTags() != null ? List.copyOf(domain.getTags()) : List.of());
        entity.setAvailability(domain.getAvailability());
        entity.setPhone(domain.getPhone());
        entity.setEmail(domain.getEmail());
        entity.setWhatsapp(domain.getWhatsapp());
        return entity;
    }

    public static Specialist toDomain(SpecialistEntity entity) {
        if (entity == null) {
            return null;
        }
        var domain = new Specialist(
                entity.getFullName(),
                entity.getRole(),
                entity.getSuccessRate(),
                entity.getCaseCount(),
                entity.getDistanceKm(),
                entity.getTags() != null ? List.copyOf(entity.getTags()) : List.of(),
                entity.getAvailability(),
                entity.getPhone(),
                entity.getEmail(),
                entity.getWhatsapp()
        );
        domain.restoreIdentity(new SpecialistId(entity.getId()));
        return domain;
    }
}
