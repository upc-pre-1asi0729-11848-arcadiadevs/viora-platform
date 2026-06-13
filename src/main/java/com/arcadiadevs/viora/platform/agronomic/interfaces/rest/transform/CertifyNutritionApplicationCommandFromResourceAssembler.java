package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.CertifyNutritionApplicationCommand;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.CertifyNutritionApplicationResource;

/**
 * Builds a {@link CertifyNutritionApplicationCommand} from its REST resource.
 */
public final class CertifyNutritionApplicationCommandFromResourceAssembler {

    private CertifyNutritionApplicationCommandFromResourceAssembler() {
    }

    public static CertifyNutritionApplicationCommand toCommandFromResource(
            Long userId,
            Long planId,
            CertifyNutritionApplicationResource resource
    ) {
        return new CertifyNutritionApplicationCommand(
                userId,
                planId,
                resource.applicationDate(),
                resource.applicationTime(),
                resource.appliedInputs(),
                resource.doseConfirmation(),
                resource.fieldOperator(),
                resource.fieldNotes()
        );
    }
}
