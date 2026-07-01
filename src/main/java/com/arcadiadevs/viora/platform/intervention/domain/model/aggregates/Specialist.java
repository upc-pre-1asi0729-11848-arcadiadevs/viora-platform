package com.arcadiadevs.viora.platform.intervention.domain.model.aggregates;

import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.SpecialistAvailability;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.SpecialistId;
import com.arcadiadevs.viora.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.util.List;

/**
 * Aggregate root representing a phytosanitary specialist available for intervention
 * requests. Holds the public profile (shown on recommendation cards) and the
 * private contact channels (revealed only after a proposal is accepted).
 */
@Getter
public class Specialist extends AbstractDomainAggregateRoot<Specialist> {

    private SpecialistId id;
    private String fullName;
    private String role;
    private Double successRate;
    private Integer caseCount;
    private Double distanceKm;
    private List<String> tags;
    private SpecialistAvailability availability;

    // Private contact channels — never exposed until a proposal is accepted.
    private String phone;
    private String email;
    private String whatsapp;

    protected Specialist() {
        // Required by JPA
    }

    public Specialist(
            String fullName,
            String role,
            Double successRate,
            Integer caseCount,
            Double distanceKm,
            List<String> tags,
            SpecialistAvailability availability,
            String phone,
            String email,
            String whatsapp) {
        this.fullName = fullName;
        this.role = role;
        this.successRate = successRate;
        this.caseCount = caseCount;
        this.distanceKm = distanceKm;
        this.tags = tags;
        this.availability = availability;
        this.phone = phone;
        this.email = email;
        this.whatsapp = whatsapp;
    }

    public Specialist restoreIdentity(SpecialistId id) {
        this.id = id;
        return this;
    }

    public boolean isAvailable() {
        return availability != null && availability.isAvailable();
    }
}
