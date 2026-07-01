package com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.entities;

import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.*;
import com.arcadiadevs.viora.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * JPA Entity representing the Intervention Outcome aggregate.
 */
@Entity
@Table(name = "intervention_outcomes")
@Getter
@Setter
public class InterventionOutcomeEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "intervention_execution_id", nullable = false)
    private InterventionExecutionId interventionExecutionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OutcomeStatus status;

    // Flattened ImpactReport
    @Column(name = "grace_period", nullable = false)
    private String gracePeriod;

    @Enumerated(EnumType.STRING)
    @Column(name = "observed_result", nullable = false)
    private ObservedResult observedResult;

    @Enumerated(EnumType.STRING)
    @Column(name = "impact_level", nullable = false)
    private ImpactLevel impactLevel;

    @Column(name = "producer_assessment", columnDefinition = "TEXT")
    private String producerAssessment;

    // Flattened ServiceEvaluation (nullable until CLOSED)
    @Enumerated(EnumType.STRING)
    @Column(name = "service_result")
    private ServiceResult serviceResult;

    @Enumerated(EnumType.STRING)
    @Column(name = "hire_again")
    private HireAgain hireAgain;

    @Column(name = "private_feedback", columnDefinition = "TEXT")
    private String privateFeedback;
}
