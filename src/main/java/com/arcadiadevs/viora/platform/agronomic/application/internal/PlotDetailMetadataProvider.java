package com.arcadiadevs.viora.platform.agronomic.application.internal;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Query-side port for persistence metadata needed by the Plot Detail screen.
 */
public interface PlotDetailMetadataProvider {

    Optional<PlotMetadata> findByPlotId(PlotId plotId);

    record PlotMetadata(
            Instant registeredAt,
            Instant lastConfigurationUpdateAt,
            MonitoringIntegrationMetadata monitoringIntegration,
            List<DeviceMetadata> devices
    ) {
    }

    record MonitoringIntegrationMetadata(
            Instant linkedAt,
            Instant lastCheckedAt,
            Instant imageryCaptureAt
    ) {
    }

    record DeviceMetadata(
            Long deviceId,
            Instant linkedAt,
            Instant lastActivityAt
    ) {
    }
}
