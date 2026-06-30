package com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources;

import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ApplicationMethod;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.PersonalProtectiveEquipment;

import java.util.List;

public record PrescribeTreatmentResource(
        ApplicationMethod applicationMethod,
        Integer sprayVolumeAmount,
        String sprayVolumeUnit,
        Integer preHarvestIntervalDays,
        String agronomistRecommendations,
        List<PersonalProtectiveEquipment> requiredPPE,
        List<PrescribedProductResource> products
) {}
