package com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.assemblers;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.TreatmentPrescription;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.*;
import com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.entities.PrescribedProductItem;
import com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.entities.TreatmentPrescriptionEntity;

public class TreatmentPrescriptionEntityAssembler {

    public static TreatmentPrescription toDomain(TreatmentPrescriptionEntity entity) {
        if (entity == null) {
            return null;
        }

        var domain = new TreatmentPrescription(entity.getServiceProposalId());
        domain.restoreIdentity(new TreatmentPrescriptionId(entity.getId()));
        domain.restoreStatus(entity.getStatus());

        if (entity.getFindingType() != null) {
            domain.restoreFieldInspection(new FieldInspectionRecord(
                    entity.getFindingType(),
                    entity.getIncidenceLevel(),
                    entity.getTechnicalDescription(),
                    entity.getRecordDate()
            ));
        }

        if (entity.getApplicationMethod() != null) {
            domain.restoreAgrochemicalPrescription(new AgrochemicalPrescription(
                    entity.getApplicationMethod(),
                    entity.getSprayVolume(),
                    entity.getPreHarvestInterval(),
                    entity.getAgronomistRecommendations(),
                    entity.getRequiredPPE(),
                    entity.getProducts() != null ? entity.getProducts().stream().map(p -> new PrescribedProduct(
                            p.getProductName(),
                            new Dosage(p.getDosageAmount(), p.getDosageUnit()),
                            new ApplicationSessions(p.getSessionsCount()),
                            p.getTechnicalRecommendation()
                    )).toList() : null
            ));
        }

        return domain;
    }

    public static TreatmentPrescriptionEntity toEntity(TreatmentPrescription domain) {
        if (domain == null) {
            return null;
        }

        var entity = new TreatmentPrescriptionEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId().value());
        }
        entity.setServiceProposalId(domain.getServiceProposalId());
        entity.setStatus(domain.getStatus());

        if (domain.getFieldInspectionRecord() != null) {
            entity.setFindingType(domain.getFieldInspectionRecord().findingType());
            entity.setIncidenceLevel(domain.getFieldInspectionRecord().incidenceLevel());
            entity.setTechnicalDescription(domain.getFieldInspectionRecord().technicalDescription());
            entity.setRecordDate(domain.getFieldInspectionRecord().recordDate());
        }

        if (domain.getAgrochemicalPrescription() != null) {
            entity.setApplicationMethod(domain.getAgrochemicalPrescription().applicationMethod());
            entity.setSprayVolume(domain.getAgrochemicalPrescription().sprayVolume());
            entity.setPreHarvestInterval(domain.getAgrochemicalPrescription().preHarvestInterval());
            entity.setAgronomistRecommendations(domain.getAgrochemicalPrescription().agronomistRecommendations());
            entity.setRequiredPPE(domain.getAgrochemicalPrescription().requiredPPE());
            
            if (domain.getAgrochemicalPrescription().products() != null) {
                entity.setProducts(domain.getAgrochemicalPrescription().products().stream().map(p ->
                        new PrescribedProductItem(
                                p.productName(), 
                                p.dosage().amount(), 
                                p.dosage().unit(), 
                                p.sessions().count(), 
                                p.technicalRecommendation()
                        )
                ).toList());
            }
        }

        return entity;
    }
}
