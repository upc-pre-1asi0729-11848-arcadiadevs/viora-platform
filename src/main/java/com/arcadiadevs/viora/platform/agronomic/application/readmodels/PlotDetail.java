package com.arcadiadevs.viora.platform.agronomic.application.readmodels;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.IoTDevice;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;

import java.time.Instant;
import java.util.List;

/**
 * Screen-oriented projection for the My Plots detail view.
 *
 * @param plot Plot aggregate.
 * @param registeredAt Time when the plot was persisted.
 * @param lastConfigurationUpdateAt Latest persisted plot configuration update.
 * @param boundaryStatus Current boundary validation state.
 * @param climateMonitoring Climate integration state.
 * @param satelliteNdvi Satellite NDVI integration state.
 * @param climateLastSyncAt Latest provider synchronization check.
 * @param satelliteLastSyncAt Latest satellite capture or provider check.
 * @param iotTelemetry IoT integration state.
 * @param onlineDeviceCount Number of devices currently active.
 * @param lastIotActivityAt Latest persisted IoT configuration activity.
 * @param devices Linked IoT devices with persistence activity timestamps.
 * @param recentConfigurationActivity Recent configuration events derived from
 *                                    persisted audit timestamps.
 */
public record PlotDetail(
        Plot plot,
        Instant registeredAt,
        Instant lastConfigurationUpdateAt,
        String boundaryStatus,
        IntegrationLinkStatus climateMonitoring,
        IntegrationLinkStatus satelliteNdvi,
        Instant climateLastSyncAt,
        Instant satelliteLastSyncAt,
        IntegrationLinkStatus iotTelemetry,
        long onlineDeviceCount,
        Instant lastIotActivityAt,
        List<DeviceDetail> devices,
        List<ConfigurationActivity> recentConfigurationActivity
) {

    public record DeviceDetail(
            IoTDevice device,
            Instant linkedAt,
            Instant lastActivityAt
    ) {
    }

    public record ConfigurationActivity(
            String type,
            String description,
            Instant occurredAt
    ) {
    }
}
