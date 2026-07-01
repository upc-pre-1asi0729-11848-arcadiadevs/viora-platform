package com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources;

import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.TreatmentPrescriptionStatus;

public record TreatmentPrescriptionResource(
        Long id,
        Long serviceProposalId,
        String status,
        LogFieldInspectionDataResource fieldInspection,
        PrescribeTreatmentResource agrochemicalPrescription
) {}
