package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.entities;

import com.arcadiadevs.viora.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entity for Plot persistence.
 * Included here as a minimal stub to satisfy foreign key constraints
 * and the ownership-check repository used by IoT device services.
 */
@Entity
@Table(name = "plots")
@Getter
@Setter
@NoArgsConstructor
public class PlotEntity extends AuditableAbstractPersistenceEntity {

    @Column(nullable = false)
    private Long ownerUserId;

    @Column(nullable = false, length = 150)
    private String plotName;
}
