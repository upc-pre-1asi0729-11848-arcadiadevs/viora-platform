package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import java.time.Instant;

/**
 * Descriptive metadata and freshness for an external monitoring data source.
 *
 * <p>
 * Surfaces the provider identity, current availability, the most recent reading
 * instant and the expected update cadence so the client can render per-source
 * "last reading" and "update frequency" indicators.
 * </p>
 *
 * @param provider Human-readable provider name (e.g. {@code "AgroMonitoring"}).
 * @param availability Current availability of the source for the plot.
 * @param lastReadingAt Instant of the most recent reading, or null when none/unknown.
 * @param updateFrequencyMinutes Expected refresh cadence in minutes, or null when unknown.
 */
public record DataSourceMetadata(
        String provider,
        ProviderDataAvailability availability,
        Instant lastReadingAt,
        Integer updateFrequencyMinutes
) {
    public DataSourceMetadata {
        if (provider == null || provider.isBlank()) {
            throw new IllegalArgumentException("Data source provider is required.");
        }
        if (availability == null) {
            throw new IllegalArgumentException("Data source availability is required.");
        }
        if (updateFrequencyMinutes != null && updateFrequencyMinutes < 0) {
            throw new IllegalArgumentException("Update frequency minutes cannot be negative.");
        }
    }

    /** Convenience factory for an unconfigured/absent source of a given provider. */
    public static DataSourceMetadata notConfigured(String provider) {
        return new DataSourceMetadata(provider, ProviderDataAvailability.NOT_CONFIGURED, null, null);
    }
}
