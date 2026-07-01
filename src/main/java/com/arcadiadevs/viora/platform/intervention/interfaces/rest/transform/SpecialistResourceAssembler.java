package com.arcadiadevs.viora.platform.intervention.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.Specialist;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.SpecialistContactResource;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.SpecialistProfileResource;

public class SpecialistResourceAssembler {

    public static SpecialistProfileResource toProfileResource(Specialist specialist) {
        return new SpecialistProfileResource(
                specialist.getId() != null ? specialist.getId().value() : null,
                specialist.getFullName(),
                specialist.getRole(),
                specialist.getSuccessRate(),
                specialist.getCaseCount(),
                specialist.getDistanceKm(),
                specialist.getTags(),
                specialist.getAvailability() != null ? specialist.getAvailability().name() : null
        );
    }

    public static SpecialistContactResource toContactResource(Specialist specialist) {
        return new SpecialistContactResource(
                specialist.getId() != null ? specialist.getId().value() : null,
                specialist.getFullName(),
                specialist.getPhone(),
                specialist.getEmail(),
                specialist.getWhatsapp()
        );
    }
}
