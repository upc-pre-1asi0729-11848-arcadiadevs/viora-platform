package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.assemblers;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.MonitoringSummary;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MitigationRecommendation;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.entities.MonitoringSummaryEntity;
import org.springframework.stereotype.Component;

/**
 * Assembler to convert a MonitoringSummary domain aggregate to a MonitoringSummaryEntity.
 */
@Component
public class MonitoringSummaryEntityFromMonitoringSummaryAssembler {

    public MonitoringSummaryEntity toEntity(MonitoringSummary aggregate) {
        MonitoringSummaryEntity entity = new MonitoringSummaryEntity();
        if (aggregate.getId() != null) {
            entity.setId(aggregate.getId().getValue());
        }
        entity.setUserId(aggregate.getUserId().getValue());
        entity.setGeneralHealthStatus(aggregate.getGeneralHealthStatus().name()); // Convert enum to String
        entity.setNdviValue(aggregate.getNdviValue().getValue());
        entity.setAccumulatedChillHours(aggregate.getAccumulatedChillHours().getValue());
        entity.setYieldForecast(aggregate.getYieldForecast().getValue());
        entity.setMeasurementDate(aggregate.getMeasurementDate().getValue());

        // New fields for WeatherSnapshot
        if (aggregate.getWeatherSnapshot() != null) {
            entity.setWeatherStatus(aggregate.getWeatherSnapshot().getWeatherStatus().name());
            entity.setWeatherMeasurementDate(aggregate.getWeatherSnapshot().getMeasurementDate().getValue());
            entity.setWeatherClimateRiskLevel(aggregate.getWeatherSnapshot().getClimateRiskLevel().name());
        }

        // New field for ClimateRiskLevel
        if (aggregate.getClimateRiskLevel() != null) {
            entity.setClimateRiskLevel(aggregate.getClimateRiskLevel().name());
        }

        // New fields for MitigationRecommendation (simplified: storing first recommendation's details)
        if (aggregate.getMitigationRecommendations() != null && !aggregate.getMitigationRecommendations().isEmpty()) {
            MitigationRecommendation firstRecommendation = aggregate.getMitigationRecommendations().get(0);
            entity.setMitigationActionType(firstRecommendation.getActionType().name());
            entity.setNutritionInputRecommendation(firstRecommendation.getNutritionInputRecommendation().getValue());
            entity.setMitigationApplicationWindowStart(firstRecommendation.getApplicationWindow().getStartDate());
            entity.setMitigationApplicationWindowEnd(firstRecommendation.getApplicationWindow().getEndDate());
        }

        return entity;
    }
}