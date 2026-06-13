package com.arcadiadevs.viora.platform.agronomic.application.queryservices;

import com.arcadiadevs.viora.platform.agronomic.application.internal.PlotDetailMetadataProvider;
import com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices.AgroMonitoringImageryService;
import com.arcadiadevs.viora.platform.agronomic.application.readmodels.IntegrationLinkStatus;
import com.arcadiadevs.viora.platform.agronomic.application.readmodels.PlotDetail;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotDetailQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.IoTDeviceStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.SatelliteImagery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.IoTDeviceRepository;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.PlotRepository;
import com.arcadiadevs.viora.platform.shared.application.result.ApplicationError;
import com.arcadiadevs.viora.platform.shared.application.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Query service dedicated to the screen-oriented Plot Detail projection.
 */
@Service
@RequiredArgsConstructor
public class PlotDetailQueryService {

    private final PlotRepository plotRepository;
    private final AgroMonitoringImageryService agroMonitoringImageryService;
    private final IoTDeviceRepository ioTDeviceRepository;
    private final PlotDetailMetadataProvider plotDetailMetadataProvider;

    /**
     * Handles the Plot Detail screen query.
     *
     * @param query Query containing the owner and plot identifiers.
     * @return Plot configuration, monitoring links, IoT status and recent
     *         persisted configuration activity.
     */
    @Transactional
    public Result<PlotDetail, ApplicationError> handle(GetPlotDetailQuery query) {
        var userId = new UserId(query.userId());
        var plotId = new PlotId(query.plotId());
        var plotOptional = plotRepository.findById(plotId);

        if (plotOptional.isEmpty() || !plotOptional.get().isActive()) {
            return Result.failure(ApplicationError.notFound(
                    "plot",
                    query.plotId().toString()
            ));
        }

        var plot = plotOptional.get();
        if (!plot.belongsTo(userId)) {
            return Result.failure(ApplicationError.forbidden(
                    "plot-ownership",
                    "User %d does not own plot %d.".formatted(
                            query.userId(),
                            query.plotId()
                    )
            ));
        }

        var imagery = agroMonitoringImageryService.findCurrentImagery(plot);
        var linkedToProvider = agroMonitoringImageryService.isPlotLinked(plot);
        var metadata = plotDetailMetadataProvider.findByPlotId(plotId)
                .orElseGet(() -> new PlotDetailMetadataProvider.PlotMetadata(
                        null,
                        null,
                        null,
                        List.of()
                ));
        var integrationMetadata = metadata.monitoringIntegration();
        var devices = ioTDeviceRepository.findAllByPlotId(plotId.getValue());
        var deviceMetadata = metadata.devices().stream()
                .collect(java.util.stream.Collectors.toMap(
                        PlotDetailMetadataProvider.DeviceMetadata::deviceId,
                        value -> value,
                        (first, second) -> first,
                        HashMap::new
                ));

        var deviceDetails = devices.stream()
                .map(device -> {
                    var timestamps = deviceMetadata.get(device.getId());
                    return new PlotDetail.DeviceDetail(
                            device,
                            timestamps == null ? null : timestamps.linkedAt(),
                            timestamps == null ? null : timestamps.lastActivityAt()
                    );
                })
                .toList();

        var onlineDeviceCount = devices.stream()
                .filter(device -> device.getStatus() == IoTDeviceStatus.ACTIVE)
                .count();
        var lastIotActivityAt = metadata.devices().stream()
                .map(PlotDetailMetadataProvider.DeviceMetadata::lastActivityAt)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);

        var climateMonitoring = linkedToProvider
                ? IntegrationLinkStatus.ACTIVE
                : IntegrationLinkStatus.NOT_LINKED;
        var satelliteNdvi = imagery.isPresent()
                ? IntegrationLinkStatus.ACTIVE
                : linkedToProvider
                ? IntegrationLinkStatus.INITIALIZING
                : IntegrationLinkStatus.NOT_LINKED;
        var iotTelemetry = devices.isEmpty()
                ? IntegrationLinkStatus.NOT_LINKED
                : IntegrationLinkStatus.ACTIVE;
        var climateLastSyncAt = linkedToProvider && integrationMetadata != null
                ? latestInstant(
                        Optional.ofNullable(integrationMetadata.lastCheckedAt()),
                        Optional.ofNullable(integrationMetadata.linkedAt())
                )
                : null;
        var persistedSatelliteSyncAt = integrationMetadata == null
                ? null
                : latestInstant(
                        Optional.ofNullable(integrationMetadata.imageryCaptureAt()),
                        Optional.ofNullable(integrationMetadata.lastCheckedAt())
                );
        var satelliteLastSyncAt = linkedToProvider
                ? latestInstant(
                        imagery.map(SatelliteImagery::captureDate),
                        Optional.ofNullable(persistedSatelliteSyncAt)
                )
                : null;

        return Result.success(new PlotDetail(
                plot,
                metadata.registeredAt(),
                metadata.lastConfigurationUpdateAt(),
                "VALIDATED",
                climateMonitoring,
                satelliteNdvi,
                climateLastSyncAt,
                satelliteLastSyncAt,
                iotTelemetry,
                onlineDeviceCount,
                lastIotActivityAt,
                deviceDetails,
                buildConfigurationActivities(
                        metadata,
                        linkedToProvider,
                        satelliteLastSyncAt
                )
        ));
    }

    private List<PlotDetail.ConfigurationActivity> buildConfigurationActivities(
            PlotDetailMetadataProvider.PlotMetadata metadata,
            boolean linkedToProvider,
            Instant satelliteLastSyncAt
    ) {
        var plotRegistered = activity(
                "PLOT_REGISTERED",
                "Plot boundary registered.",
                metadata.registeredAt()
        );
        var plotUpdated = metadata.lastConfigurationUpdateAt() != null
                && metadata.registeredAt() != null
                && metadata.lastConfigurationUpdateAt().isAfter(metadata.registeredAt())
                ? activity(
                        "PLOT_CONFIGURATION_UPDATED",
                        "Plot configuration updated.",
                        metadata.lastConfigurationUpdateAt()
                )
                : Stream.<PlotDetail.ConfigurationActivity>empty();
        var climateLinked = linkedToProvider && metadata.monitoringIntegration() != null
                ? activity(
                        "CLIMATE_MONITORING_LINKED",
                        "Climate monitoring linked.",
                        metadata.monitoringIntegration().linkedAt()
                )
                : Stream.<PlotDetail.ConfigurationActivity>empty();
        var satelliteSynchronized = linkedToProvider
                ? activity(
                        "SATELLITE_MONITORING_SYNCHRONIZED",
                        "Satellite monitoring synchronized.",
                        satelliteLastSyncAt
                )
                : Stream.<PlotDetail.ConfigurationActivity>empty();
        var devicesLinked = metadata.devices().stream()
                .flatMap(device -> activity(
                        "IOT_DEVICE_LINKED",
                        "IoT device %d linked.".formatted(device.deviceId()),
                        device.linkedAt()
                ));

        return Stream.of(
                        plotRegistered,
                        plotUpdated,
                        climateLinked,
                        satelliteSynchronized,
                        devicesLinked
                )
                .flatMap(stream -> stream)
                .sorted(Comparator.comparing(
                        PlotDetail.ConfigurationActivity::occurredAt
                ).reversed())
                .limit(10)
                .toList();
    }

    private Stream<PlotDetail.ConfigurationActivity> activity(
            String type,
            String description,
            Instant occurredAt
    ) {
        return occurredAt == null
                ? Stream.empty()
                : Stream.of(new PlotDetail.ConfigurationActivity(
                        type,
                        description,
                        occurredAt
                ));
    }

    private Instant latestInstant(Optional<Instant> first, Optional<Instant> second) {
        return first
                .map(value -> second.filter(other -> other.isAfter(value)).orElse(value))
                .or(() -> second)
                .orElse(null);
    }
}
