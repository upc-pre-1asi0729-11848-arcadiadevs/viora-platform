package com.arcadiadevs.viora.platform.intervention.domain.services;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.Specialist;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.SpecialistAvailability;
import com.arcadiadevs.viora.platform.intervention.domain.repositories.SpecialistRepository;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.SpecialistCandidateResource;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * Domain policy for matching specialist candidates to an alert. Ranks the seeded
 * specialist catalog by availability first, then by success rate and proximity.
 */
@Service
public class SpecialistMatchingPolicy {

    private final SpecialistRepository specialistRepository;

    public SpecialistMatchingPolicy(SpecialistRepository specialistRepository) {
        this.specialistRepository = specialistRepository;
    }

    /**
     * Matches and ranks specialists for a given alert.
     *
     * @param alertId the alert ID (reserved for future geo/threat-aware ranking)
     * @param limit   the maximum number of candidates to return
     * @return the best-matched specialist candidates
     */
    public List<SpecialistCandidateResource> matchSpecialistsForAlert(Long alertId, Integer limit) {
        int cap = (limit == null || limit <= 0) ? 3 : limit;

        return specialistRepository.findAll().stream()
                .sorted(
                        Comparator.<Specialist>comparingInt(s -> availabilityRank(s.getAvailability()))
                                .thenComparing(Comparator.comparingDouble(Specialist::getSuccessRate).reversed())
                                .thenComparing(Comparator.comparingDouble(Specialist::getDistanceKm))
                )
                .limit(cap)
                .map(this::toResource)
                .toList();
    }

    private SpecialistCandidateResource toResource(Specialist specialist) {
        return new SpecialistCandidateResource(
                specialist.getId() != null ? specialist.getId().value() : null,
                specialist.getFullName(),
                specialist.getRole(),
                specialist.getSuccessRate(),
                specialist.getCaseCount(),
                specialist.getDistanceKm(),
                specialist.getTags(),
                specialist.getAvailability() != null ? specialist.getAvailability().name() : null,
                specialist.isAvailable()
        );
    }

    private int availabilityRank(SpecialistAvailability availability) {
        if (availability == null) {
            return 99;
        }
        return switch (availability) {
            case AVAILABLE_TODAY -> 0;
            case AVAILABLE_TOMORROW -> 1;
            case AVAILABLE_THIS_WEEK -> 2;
            case UNAVAILABLE -> 3;
        };
    }
}
