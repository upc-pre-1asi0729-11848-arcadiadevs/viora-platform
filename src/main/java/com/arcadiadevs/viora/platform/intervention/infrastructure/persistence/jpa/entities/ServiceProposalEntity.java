package com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.entities;

import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.CostEstimate;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.InterventionRequestId;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ServiceProposalStatus;
import com.arcadiadevs.viora.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "service_proposals")
@Getter
@Setter
public class ServiceProposalEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "intervention_request_id", nullable = false)
    private InterventionRequestId interventionRequestId;

    @Column(name = "specialist_id", nullable = false)
    private Long specialistId;

    @Column(name = "proposed_date", nullable = false)
    private Date proposedDate;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "amount", nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "currency", nullable = false, length = 10))
    })
    private CostEstimate costEstimate;

    @Column(name = "proposal_details", nullable = false, columnDefinition = "TEXT")
    private String proposalDetails;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ServiceProposalStatus status;
}
