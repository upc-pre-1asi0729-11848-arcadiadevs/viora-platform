package com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.entities;

import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.SpecialistAvailability;
import com.arcadiadevs.viora.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * JPA entity for the Specialist aggregate.
 */
@Entity
@Table(name = "specialists")
@Getter
@Setter
public class SpecialistEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "success_rate", nullable = false)
    private Double successRate;

    @Column(name = "case_count", nullable = false)
    private Integer caseCount;

    @Column(name = "distance_km", nullable = false)
    private Double distanceKm;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "specialist_tags", joinColumns = @JoinColumn(name = "specialist_id"))
    @Column(name = "tag")
    private List<String> tags;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability", nullable = false)
    private SpecialistAvailability availability;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "whatsapp")
    private String whatsapp;
}
