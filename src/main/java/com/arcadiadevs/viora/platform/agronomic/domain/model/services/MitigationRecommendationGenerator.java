package com.arcadiadevs.viora.platform.agronomic.domain.model.services;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ClimateRiskLevel;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MitigationActionType;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MitigationRecommendation;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NutritionInputRecommendation;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.TimeWindow;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Domain service for generating mitigation recommendations based on climate risk.
 */
@Service
public class MitigationRecommendationGenerator {

    /**
     * Generates a list of mitigation recommendations based on the current climate risk level.
     *
     * @param climateRiskLevel The current climate risk level.
     * @return A list of mitigation recommendations.
     */
    public List<MitigationRecommendation> generateRecommendations(ClimateRiskLevel climateRiskLevel) {
        List<MitigationRecommendation> recommendations = new ArrayList<>();
        LocalDate today = LocalDate.now();

        switch (climateRiskLevel) {
            case LOW:
                recommendations.add(new MitigationRecommendation(
                        MitigationActionType.OTHER,
                        new NutritionInputRecommendation("Monitor soil moisture"),
                        new TimeWindow(today, today.plusDays(7)),
                        climateRiskLevel
                ));
                break;
            case MODERATE:
                recommendations.add(new MitigationRecommendation(
                        MitigationActionType.IRRIGATION,
                        new NutritionInputRecommendation("Check irrigation system efficiency"),
                        new TimeWindow(today, today.plusDays(3)),
                        climateRiskLevel
                ));
                recommendations.add(new MitigationRecommendation(
                        MitigationActionType.NUTRIENT_APPLICATION,
                        new NutritionInputRecommendation("Apply balanced NPK fertilizer"),
                        new TimeWindow(today.plusDays(1), today.plusDays(5)),
                        climateRiskLevel
                ));
                break;
            case HIGH:
                recommendations.add(new MitigationRecommendation(
                        MitigationActionType.PEST_CONTROL,
                        new NutritionInputRecommendation("Scout for pests and apply organic pesticide"),
                        new TimeWindow(today, today.plusDays(1)),
                        climateRiskLevel
                ));
                recommendations.add(new MitigationRecommendation(
                        MitigationActionType.IRRIGATION,
                        new NutritionInputRecommendation("Increase irrigation frequency and volume"),
                        new TimeWindow(today, today.plusDays(2)),
                        climateRiskLevel
                ));
                break;
            case EXTREME:
                recommendations.add(new MitigationRecommendation(
                        MitigationActionType.DISEASE_CONTROL,
                        new NutritionInputRecommendation("Apply broad-spectrum fungicide immediately"),
                        new TimeWindow(today, today.plusDays(1)),
                        climateRiskLevel
                ));
                recommendations.add(new MitigationRecommendation(
                        MitigationActionType.SOIL_TREATMENT,
                        new NutritionInputRecommendation("Consider soil amendments for stress reduction"),
                        new TimeWindow(today.plusDays(1), today.plusDays(3)),
                        climateRiskLevel
                ));
                break;
            case UNKNOWN:
            default:
                recommendations.add(new MitigationRecommendation(
                        MitigationActionType.OTHER,
                        new NutritionInputRecommendation("Gather more data for accurate assessment"),
                        new TimeWindow(today, today.plusDays(1)),
                        climateRiskLevel
                ));
                break;
        }

        return recommendations;
    }
}
