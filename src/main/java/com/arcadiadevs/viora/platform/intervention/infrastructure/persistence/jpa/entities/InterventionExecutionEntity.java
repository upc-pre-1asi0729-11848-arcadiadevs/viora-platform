package com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.entities;

import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ExecutionStatus;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.TreatmentPrescriptionId;
import com.arcadiadevs.viora.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * JPA Entity representing the Intervention Execution (Application Certification).
 */
@Entity
@Table(name = "intervention_executions")
@Getter
@Setter
public class InterventionExecutionEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "treatment_prescription_id", nullable = false)
    private TreatmentPrescriptionId treatmentPrescriptionId;

    @Column(name = "application_date", nullable = false)
    private Date applicationDate;

    @Column(name = "applied_area", nullable = false)
    private String appliedArea;

    @Enumerated(EnumType.STRING)
    @Column(name = "execution_status", nullable = false)
    private ExecutionStatus executionStatus;

    @Column(name = "field_note", columnDefinition = "TEXT")
    private String fieldNote;
}
