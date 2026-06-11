package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.MonitoringSummary;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MitigationRecommendation;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.MitigationRecommendationResource;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.MonitoringSummaryResource;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.WeatherSnapshotResource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Assembler to convert a MonitoringSummary domain aggregate to a MonitoringSummaryResource.
 */
@Component
public class MonitoringSummaryResourceFromMonitoringSummaryAssembler {

    public MonitoringSummaryResource toResource(MonitoringSummary aggregate) {
        WeatherSnapshotResource weatherSnapshotResource = null;
        if (aggregate.getWeatherSnapshot() != null) {
            weatherSnapshotResource = new WeatherSnapshotResource(
                    aggregate.getWeatherSnapshot().getWeatherStatus().name(),
                    aggregate.getWeatherSnapshot().getMeasurementDate().getValue(),
                    aggregate.getWeatherSnapshot().getClimateRiskLevel().name()
            );
        }

        List<MitigationRecommendationResource> mitigationRecommendationResources = aggregate.getMitigationRecommendations().stream()
                .map(this::toMitigationRecommendationResource)
                .collect(Collectors.toList());

        return new MonitoringSummaryResource(
                aggregate.getId() != null ? aggregate.getId().getValue() : null,
                aggregate.getUserId().getValue(),
                aggregate.getGeneralHealthStatus().name(),
                aggregate.getNdviValue().getValue(),
                aggregate.getAccumulatedChillHours().getValue(),
                aggregate.getYieldForecast().getValue(),
                aggregate.getMeasurementDate().getValue(),
                weatherSnapshotResource,
                aggregate.getClimateRiskLevel().name(),
                mitigationRecommendationResources
        );
    }

    private MitigationRecommendationResource toMitigationRecommendationResource(MitigationRecommendation recommendation) {
        return new MitigationRecommendationResource(
                recommendation.getActionType().name(),
                recommendation.getNutritionInputRecommendation().getValue(),
                recommendation.getApplicationWindow().getStartDate(),
                recommendation.getApplicationWindow().getEndDate()
        );
    }
}