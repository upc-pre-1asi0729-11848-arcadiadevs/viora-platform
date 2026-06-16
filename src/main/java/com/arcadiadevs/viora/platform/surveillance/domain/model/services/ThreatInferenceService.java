package com.arcadiadevs.viora.platform.surveillance.domain.model.services;

import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.Symptoms;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.ThreatType;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Domain service responsible for inferring specific biological threats based on observed symptoms.
 */
@Service
public class ThreatInferenceService {

    /**
     * Analyzes the reported symptoms and infers the most probable biological threat.
     * Uses heuristic scoring based on known olive grove pests and diseases.
     * 
     * @param symptoms The symptoms observed in the plot.
     * @return The most probable ThreatType.
     */
    public ThreatType inferFromSymptoms(Symptoms symptoms) {
        if (symptoms == null || symptoms.getDescriptions() == null || symptoms.getDescriptions().isEmpty()) {
            return ThreatType.UNKNOWN;
        }

        List<String> descriptions = symptoms.getDescriptions().stream()
                .map(String::toLowerCase)
                .toList();

        int xylellaScore = 0;
        int fruitFlyScore = 0;
        int oliveMothScore = 0;
        int peacockSpotScore = 0;

        for (String desc : descriptions) {
            // Xylella fastidiosa heuristics
            if (desc.contains("yellowing") || desc.contains("branch drying") || desc.contains("dieback") || desc.contains("scorched")) {
                xylellaScore += 2;
            }
            if (desc.contains("low-vigor") || desc.contains("leaf drop")) {
                xylellaScore += 1;
            }

            // Bactrocera oleae (Olive fruit fly)
            if (desc.contains("fruit rot") || desc.contains("puncture") || desc.contains("fruit drop")) {
                fruitFlyScore += 2;
            }
            if (desc.contains("worm") || desc.contains("larvae")) {
                fruitFlyScore += 1;
            }

            // Prays oleae (Olive moth)
            if (desc.contains("flower web") || desc.contains("tunneling") || desc.contains("mining")) {
                oliveMothScore += 2;
            }

            // Spilocaea oleaginea (Peacock spot)
            if (desc.contains("ring spot") || desc.contains("dark spot") || desc.contains("peacock")) {
                peacockSpotScore += 2;
            }
            if (desc.contains("defoliation")) {
                peacockSpotScore += 1;
            }
        }

        // Determine the highest score
        int maxScore = Math.max(Math.max(xylellaScore, fruitFlyScore), Math.max(oliveMothScore, peacockSpotScore));

        if (maxScore == 0) {
            return ThreatType.PEST_SYMPTOM; // General pest symptom if no specific pattern matches
        }

        if (maxScore == xylellaScore) return ThreatType.XYLELLA_RELATED;
        if (maxScore == fruitFlyScore) return ThreatType.OLIVE_FRUIT_FLY;
        if (maxScore == oliveMothScore) return ThreatType.OLIVE_MOTH;
        return ThreatType.PEACOCK_SPOT;
    }
}
