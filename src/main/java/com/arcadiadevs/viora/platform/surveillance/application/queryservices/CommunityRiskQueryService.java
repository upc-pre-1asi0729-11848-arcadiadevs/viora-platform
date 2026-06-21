package com.arcadiadevs.viora.platform.surveillance.application.queryservices;

import com.arcadiadevs.viora.platform.surveillance.application.internal.outboundservices.acl.ExternalAgronomicService;
import com.arcadiadevs.viora.platform.surveillance.domain.model.queries.GetCommunityRiskByPlotQuery;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.AlertStatus;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.ThreatType;
import com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.entities.AlertEntity;
import com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.repositories.SpringDataAlertRepository;
import com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources.CommunityRiskResource;
import com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources.NearbyRiskSignalResource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Builds the anonymized community-risk snapshot around a reference plot.
 *
 * <p>Signals are derived from the <em>active</em> alerts of neighbor plots within
 * the requested radius. Neighbor identities are intentionally dropped: each
 * signal exposes only the probable threat, severity and approximate distance, so
 * a producer can anticipate nearby threats without learning whose plot raised
 * them.</p>
 */
@Service
@RequiredArgsConstructor
public class CommunityRiskQueryService {

    private final ExternalAgronomicService externalAgronomicService;
    private final SpringDataAlertRepository springDataAlertRepository;

    /**
     * Handles {@link GetCommunityRiskByPlotQuery}.
     *
     * @param query the query carrying the reference plot and radius
     * @return the community-risk snapshot, or empty if the plot does not exist
     */
    public Optional<CommunityRiskResource> handle(GetCommunityRiskByPlotQuery query) {
        var plotName = externalAgronomicService.getPlotName(query.plotId());

        if (plotName.isEmpty()) {
            return Optional.empty();
        }

        var neighbors = externalAgronomicService.findNeighborPlotsWithinRadius(
                query.plotId(), query.radiusKm());

        if (neighbors.isEmpty()) {
            return Optional.of(new CommunityRiskResource(
                    query.plotId(), plotName.get(), query.radiusKm(), List.of(),
                    preventiveRecommendations(List.of())));
        }

        Map<Long, Double> distanceByPlotId = neighbors.stream()
                .collect(Collectors.toMap(
                        neighbor -> neighbor.plotId(),
                        neighbor -> neighbor.distanceKm(),
                        (first, second) -> first));

        var activeAlerts = springDataAlertRepository.findByPlotIdInAndStatus(
                List.copyOf(distanceByPlotId.keySet()), AlertStatus.ACTIVE);

        var signals = buildSignals(activeAlerts, distanceByPlotId);

        return Optional.of(new CommunityRiskResource(
                query.plotId(),
                plotName.get(),
                query.radiusKm(),
                signals,
                preventiveRecommendations(signals)));
    }

    private List<NearbyRiskSignalResource> buildSignals(
            List<AlertEntity> alerts, Map<Long, Double> distanceByPlotId) {
        var sorted = alerts.stream()
                .sorted((first, second) ->
                        Double.compare(
                                distanceByPlotId.getOrDefault(first.getPlotId(), Double.MAX_VALUE),
                                distanceByPlotId.getOrDefault(second.getPlotId(), Double.MAX_VALUE)))
                .toList();

        return java.util.stream.IntStream.range(0, sorted.size())
                .mapToObj(index -> {
                    var alert = sorted.get(index);
                    var threatLabel = threatLabel(alert.getType());
                    return new NearbyRiskSignalResource(
                            "signal-" + (index + 1),
                            "Possible " + threatLabel + " detected nearby",
                            threatLabel,
                            alert.getSeverity() != null ? alert.getSeverity().name() : "MEDIUM",
                            distanceByPlotId.getOrDefault(alert.getPlotId(), 0.0));
                })
                .toList();
    }

    /** Producer-facing label for a threat type used in anonymized signals. */
    private String threatLabel(ThreatType type) {
        if (type == null) {
            return "agronomic threat";
        }

        return switch (type) {
            case XYLELLA_RELATED -> "Xylella-related";
            case OLIVE_FRUIT_FLY -> "olive fruit fly";
            case OLIVE_MOTH -> "olive moth";
            case PEACOCK_SPOT -> "peacock spot";
            case PHENOLOGICAL_RISK -> "phenological imbalance";
            case CHILL_DEFICIT, CLIMATE_EXTREME -> "climate stress";
            case LOW_NDVI -> "low-vigor cluster";
            case HYDRIC_STRESS, WATER_STRESS -> "hydric stress";
            case PEST_SYMPTOM, COMMUNITY_PEST -> "pest symptom";
            default -> "agronomic threat";
        };
    }

    /** Derives preventive recommendations from the detected signals. */
    private List<String> preventiveRecommendations(List<NearbyRiskSignalResource> signals) {
        if (signals.isEmpty()) {
            return List.of("No nearby community risk signals detected within the selected radius.");
        }

        return List.of(
                "Recommended: inspect leaves and monitor low-vigor zones",
                "Prioritize nearby plots for inspection",
                "Review low-vigor zones in recent vegetation data",
                "Escalate to field inspection if symptoms persist");
    }
}
