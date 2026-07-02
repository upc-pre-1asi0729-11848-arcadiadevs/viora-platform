package com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.entities;

import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.*;
import com.arcadiadevs.viora.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * JPA Entity representing the Treatment Prescription aggregate.
 * Flattens the domain value objects (FieldInspectionRecord, AgrochemicalPrescription)
 * into a single relational table, avoiding persistence annotations in the domain.
 */
@Entity
@Table(name = "treatment_prescriptions")
@Getter
@Setter
public class TreatmentPrescriptionEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "service_proposal_id", nullable = false)
    private Long serviceProposalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TreatmentPrescriptionStatus status;

    // Flattened FieldInspectionRecord
    @Enumerated(EnumType.STRING)
    @Column(name = "finding_type")
    private FindingType findingType;

    @Enumerated(EnumType.STRING)
    @Column(name = "incidence_level")
    private IncidenceLevel incidenceLevel;

    @Column(name = "technical_description", columnDefinition = "TEXT")
    private String technicalDescription;

    @Column(name = "record_date")
    private Date recordDate;

    // Flattened AgrochemicalPrescription
    @Enumerated(EnumType.STRING)
    @Column(name = "application_method")
    private ApplicationMethod applicationMethod;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "spray_volume_amount")),
        @AttributeOverride(name = "unit", column = @Column(name = "spray_volume_unit"))
    })
    private SprayVolume sprayVolume;

    @Column(name = "pre_harvest_interval_days")
    private PreHarvestInterval preHarvestInterval;

    @Column(name = "agronomist_recommendations", columnDefinition = "TEXT")
    private String agronomistRecommendations;

    @ElementCollection
    @CollectionTable(name = "treatment_prescription_ppe", joinColumns = @JoinColumn(name = "treatment_prescription_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "ppe")
    private List<PersonalProtectiveEquipment> requiredPPE;

    @ElementCollection
    @CollectionTable(name = "treatment_prescription_products", joinColumns = @JoinColumn(name = "treatment_prescription_id"))
    private List<PrescribedProductItem> products;
}
