package com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.entities;

import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.InterventionStatus;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ReferenceCode;
import com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.converters.ReferenceCodeAttributeConverter;
import com.arcadiadevs.viora.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA Entity for mapping the InterventionRequest domain aggregate to the database.
 */
@Entity
@Table(name = "intervention_requests")
@Getter
@Setter
@NoArgsConstructor
public class InterventionRequestEntity extends AuditableAbstractPersistenceEntity {

    @Convert(converter = ReferenceCodeAttributeConverter.class)
    @Column(nullable = false, unique = true)
    private ReferenceCode referenceCode;

    @Column(nullable = false)
    private Long growerId;

    @Column(nullable = false)
    private Long plotId;

    @Column(nullable = false)
    private Long specialistId;

    @Column(nullable = false)
    private Long alertId;

    @Column(nullable = false, length = 1000)
    private String reason;

    @Column(length = 2000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterventionStatus status;

    @Column(length = 1000)
    private String declineReason;
}
