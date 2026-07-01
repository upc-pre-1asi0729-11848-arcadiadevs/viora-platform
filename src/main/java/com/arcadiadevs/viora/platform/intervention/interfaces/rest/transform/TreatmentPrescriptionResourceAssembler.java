package com.arcadiadevs.viora.platform.intervention.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.TreatmentPrescription;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.CreateTreatmentPrescriptionCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.LogFieldInspectionDataCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.PrescribeTreatmentCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.*;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.*;

public class TreatmentPrescriptionResourceAssembler {

    public static TreatmentPrescriptionResource toResourceFromDomain(TreatmentPrescription domain) {
        if (domain == null) return null;

        LogFieldInspectionDataResource inspectionResource = null;
        if (domain.getFieldInspectionRecord() != null) {
            inspectionResource = new LogFieldInspectionDataResource(
                    domain.getFieldInspectionRecord().findingType(),
                    domain.getFieldInspectionRecord().incidenceLevel(),
                    domain.getFieldInspectionRecord().technicalDescription(),
                    domain.getFieldInspectionRecord().recordDate()
            );
        }

        PrescribeTreatmentResource prescriptionResource = null;
        if (domain.getAgrochemicalPrescription() != null) {
            prescriptionResource = new PrescribeTreatmentResource(
                    domain.getAgrochemicalPrescription().applicationMethod(),
                    domain.getAgrochemicalPrescription().sprayVolume().amount(),
                    domain.getAgrochemicalPrescription().sprayVolume().unit(),
                    domain.getAgrochemicalPrescription().preHarvestInterval().days(),
                    domain.getAgrochemicalPrescription().agronomistRecommendations(),
                    domain.getAgrochemicalPrescription().requiredPPE(),
                    domain.getAgrochemicalPrescription().products().stream().map(p ->
                            new PrescribedProductResource(p.productName(), p.dosage().amount(), p.dosage().unit(), p.sessions().count(), p.technicalRecommendation())
                    ).toList()
            );
        }

        return new TreatmentPrescriptionResource(
                domain.getId().value(),
                domain.getServiceProposalId().value(),
                domain.getStatus() != null ? domain.getStatus().name() : null,
                inspectionResource,
                prescriptionResource
        );
    }

    public static CreateTreatmentPrescriptionCommand toCommandFromResource(CreateTreatmentPrescriptionResource resource) {
        return new CreateTreatmentPrescriptionCommand(resource.serviceProposalId());
    }

    public static LogFieldInspectionDataCommand toCommandFromResource(Long treatmentPrescriptionId, LogFieldInspectionDataResource resource) {
        return new LogFieldInspectionDataCommand(
                treatmentPrescriptionId,
                resource.findingType(),
                resource.incidenceLevel(),
                resource.technicalDescription(),
                resource.recordDate()
        );
    }

    public static PrescribeTreatmentCommand toCommandFromResource(Long treatmentPrescriptionId, PrescribeTreatmentResource resource) {
        return new PrescribeTreatmentCommand(
                treatmentPrescriptionId,
                resource.applicationMethod(),
                new SprayVolume(resource.sprayVolumeAmount(), resource.sprayVolumeUnit()),
                new PreHarvestInterval(resource.preHarvestIntervalDays()),
                resource.agronomistRecommendations(),
                resource.requiredPPE(),
                resource.products().stream().map(p -> new PrescribedProduct(
                        p.productName(),
                        new Dosage(p.dosageAmount(), p.dosageUnit()),
                        new ApplicationSessions(p.sessionsCount()),
                        p.technicalRecommendation()
                )).toList()
        );
    }
}
