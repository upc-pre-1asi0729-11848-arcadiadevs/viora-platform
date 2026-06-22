package com.arcadiadevs.viora.platform.surveillance.application.queryservices;

import com.arcadiadevs.viora.platform.surveillance.domain.model.aggregates.Alert;
import com.arcadiadevs.viora.platform.surveillance.domain.repositories.AlertRepository;
import com.arcadiadevs.viora.platform.surveillance.domain.model.queries.GetAlertByIdQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.repositories.SpringDataAlertRepository;
import com.arcadiadevs.viora.platform.surveillance.infrastructure.outbound.agronomic.AgronomicContextFacade;
import com.arcadiadevs.viora.platform.surveillance.domain.model.queries.GetRecentAlertsByUserIdQuery;
import com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources.AlertSummaryResource;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertQueryService {

    private final AlertRepository alertRepository;
    private final SpringDataAlertRepository springDataAlertRepository;
    private final AgronomicContextFacade agronomicContextFacade;

    public Optional<Alert> handle(GetAlertByIdQuery query) {
        return alertRepository.findById(query.alertId());
    }

    public List<AlertSummaryResource> handle(GetRecentAlertsByUserIdQuery query) {
        var plotSummaries = agronomicContextFacade.getPlotsForUserAsMap(query.userId());
        
        if (plotSummaries.isEmpty()) {
            return List.of();
        }

        var plotIds = plotSummaries.keySet().stream().toList();
        var pageable = PageRequest.of(0, query.limit());
        // Dismissed alerts (e.g. reports ruled out after inspection) drop out of the panel.
        var alertEntities = springDataAlertRepository.findByPlotIdInAndStatusNotOrderByCreatedAtDesc(
                plotIds,
                com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.AlertStatus.DISMISSED,
                pageable);

        return alertEntities.stream().map(entity -> {
            var plotSummary = plotSummaries.get(entity.getPlotId());
            return new AlertSummaryResource(
                    entity.getId(),
                    entity.getType() != null ? entity.getType().name() : "Unknown",
                    entity.getTitle() != null ? entity.getTitle() : entity.getRiskExplanation(),
                    entity.getSeverity() != null ? entity.getSeverity().name() : "Unknown",
                    entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : "",
                    entity.getStatus() != null ? entity.getStatus().name() : "Unknown",
                    entity.getSources() != null
                            ? entity.getSources().stream().map(Enum::name).collect(Collectors.toList())
                            : List.of(),
                    entity.getPlotId(),
                    plotSummary
            );
        }).collect(Collectors.toList());
    }
}
