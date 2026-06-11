package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.assemblers;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.MonitoringSummary;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AccumulatedChillHours;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ClimateRiskLevel;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeneralHealthStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MeasurementDate;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MitigationActionType;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MitigationRecommendation;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MonitoringSummaryId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviValue;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NutritionInputRecommendation;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.TimeWindow;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherSnapshot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.YieldForecast;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.entities.MonitoringSummaryEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Assembler to convert a MonitoringSummaryEntity to a MonitoringSummary domain aggregate.
 */
@Component
public class MonitoringSummaryFromMonitoringSummaryEntityAssembler {

    public MonitoringSummary toDomain(MonitoringSummaryEntity entity) {
        // Reconstruct WeatherSnapshot
        WeatherSnapshot weatherSnapshot = new WeatherSnapshot(
                WeatherStatus.fromString(entity.getWeatherStatus()),
                new MeasurementDate(entity.getWeatherMeasurementDate()),
                ClimateRiskLevel.fromString(entity.getWeatherClimateRiskLevel()),
                entity.getWeatherTemperature() // New field
        );

        // Reconstruct ClimateRiskLevel
        ClimateRiskLevel climateRiskLevel = ClimateRiskLevel.fromString(entity.getClimateRiskLevel());

        // Reconstruct MitigationRecommendations (simplified: only the first one if present)
        List<MitigationRecommendation> mitigationRecommendations = new ArrayList<>();
        if (entity.getMitigationActionType() != null && entity.getNutritionInputRecommendation() != null &&
            entity.getMitigationApplicationWindowStart() != null && entity.getMitigationApplicationWindowEnd() != null) {
            
            // Note: ClimateRiskLevel is passed for validation during construction, not stored in MitigationRecommendation
            // We need a dummy ClimateRiskLevel for this reconstruction, or fetch the actual one if available.
            // For simplicity, we'll use the climateRiskLevel derived from the entity.
            MitigationRecommendation recommendation = new MitigationRecommendation(
                    MitigationActionType.fromString(entity.getMitigationActionType()),
                    new NutritionInputRecommendation(entity.getNutritionInputRecommendation()),
                    new TimeWindow(entity.getMitigationApplicationWindowStart(), entity.getMitigationApplicationWindowEnd()),
                    climateRiskLevel // Pass for validation
            );
            mitigationRecommendations.add(recommendation);
        }

        // Create the MonitoringSummary with all fields
        MonitoringSummary monitoringSummary = new MonitoringSummary(
                new UserId(entity.getUserId()),
                GeneralHealthStatus.fromString(entity.getGeneralHealthStatus()),
                new NdviValue(entity.getNdviValue()),
                new AccumulatedChillHours(entity.getAccumulatedChillHours()),
                new YieldForecast(entity.getYieldForecast()),
                new MeasurementDate(entity.getMeasurementDate()),
                weatherSnapshot,
                climateRiskLevel,
                mitigationRecommendations
        );
        
        // Restore the identity if it exists
        if (entity.getId() != null) {
            monitoringSummary.restoreIdentity(new MonitoringSummaryId(entity.getId()));
        }
        
        return monitoringSummary;
    }
}