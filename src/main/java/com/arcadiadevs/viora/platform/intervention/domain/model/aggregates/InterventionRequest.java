package com.arcadiadevs.viora.platform.intervention.domain.model.aggregates;

import com.arcadiadevs.viora.platform.intervention.domain.model.commands.CreateInterventionRequestCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.InterventionRequestId;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.InterventionStatus;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ReferenceCode;
import com.arcadiadevs.viora.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.util.Date;

/**
 * Aggregate root for Intervention Request.
 * Domain logic only, without persistence annotations.
 */
@Getter
public class InterventionRequest extends AbstractDomainAggregateRoot<InterventionRequest> {

    private InterventionRequestId id;
    private ReferenceCode referenceCode;
    
    // External Bounded Context Identifiers
    private Long growerId;
    private Long plotId;
    private Long specialistId;
    private Long alertId;

    private String reason;
    private String message;
    private InterventionStatus status;
    private String declineReason;
    private Date createdAt;
    private Date updatedAt;

    public InterventionRequest() {
        // Default constructor
    }

    public InterventionRequest(Long growerId, Long plotId, Long specialistId, Long alertId, String reason, String message) {
        this.referenceCode = ReferenceCode.generate();
        this.growerId = growerId;
        this.plotId = plotId;
        this.specialistId = specialistId;
        this.alertId = alertId;
        this.reason = reason;
        this.message = message;
        this.status = InterventionStatus.PENDING;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public InterventionRequest(CreateInterventionRequestCommand command) {
        this.referenceCode = ReferenceCode.generate();
        this.growerId = command.growerId();
        this.plotId = command.plotId();
        this.specialistId = command.specialistId();
        this.alertId = command.alertId();
        this.reason = command.reason();
        this.message = command.message();
        this.status = InterventionStatus.PENDING;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public InterventionRequest restoreIdentity(InterventionRequestId id) {
        if (id == null) {
            throw new IllegalArgumentException("Intervention request ID is required.");
        }
        if (this.id != null && !this.id.equals(id)) {
            throw new IllegalStateException("Intervention request identity cannot be changed.");
        }

        this.id = id;
        return this;
    }

    public void decline(String reason) {
        this.status = InterventionStatus.DECLINED;
        this.declineReason = reason;
        this.updatedAt = new Date();
    }

    /**
     * Restores the persisted lifecycle state of the request. Used by infrastructure
     * assemblers when reconstructing the aggregate from storage, so the real status
     * and audit timestamps are preserved instead of being reset to a fresh request.
     *
     * @param status        the persisted status
     * @param declineReason the persisted decline reason (nullable)
     * @param createdAt     the persisted creation timestamp
     * @param updatedAt     the persisted last-update timestamp
     * @return this aggregate, for chaining
     */
    public InterventionRequest restoreState(InterventionStatus status, String declineReason, Date createdAt, Date updatedAt) {
        this.status = status;
        this.declineReason = declineReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        return this;
    }
}
