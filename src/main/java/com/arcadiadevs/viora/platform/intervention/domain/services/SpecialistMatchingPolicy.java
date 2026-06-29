package com.arcadiadevs.viora.platform.intervention.domain.services;

import org.springframework.stereotype.Service;
import java.util.List;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.SpecialistCandidateResource;

/**
 * Domain policy for matching specialist candidates to an alert based on proximity and suitability.
 */
@Service
public class SpecialistMatchingPolicy {

    /**
     * Matches and filters specialists for a given alert.
     * 
     * @param alertId the alert ID
     * @param limit the maximum number of candidates to return
     * @return list of best matched specialist candidates
     */
    public List<SpecialistCandidateResource> matchSpecialistsForAlert(Long alertId, Integer limit) {
        // TODO: Implement actual logic:
        // 1. Get plot location from alertId via AlertContextClient
        // 2. Query available specialists near the location via SpecialistProfileClient
        // 3. Filter and rank based on distance, success rate, etc.
        
        return List.of(
            new SpecialistCandidateResource(
                890L, 
                "Valeria Rojas", 
                95.5, 
                24, 
                6.4, 
                "8 years", 
                List.of("Xylella monitoring", "Biological stress"), 
                true
            )
        );
    }
}
