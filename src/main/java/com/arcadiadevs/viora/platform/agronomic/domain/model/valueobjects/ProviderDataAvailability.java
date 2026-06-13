package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

/**
 * Availability of an external monitoring data source for a plot.
 *
 * <p>
 * Lets the platform describe, per source, why data may be missing instead of
 * collapsing every case into an empty result.
 * </p>
 */
public enum ProviderDataAvailability {

    /** The source is configured, linked and able to deliver data. */
    AVAILABLE,

    /** The source is configured but the provider could not be reached or returned no data. */
    UNAVAILABLE,

    /** The provider rejected the request because the account quota is exhausted. */
    QUOTA_EXCEEDED,

    /** The integration is not enabled or has no credentials configured. */
    NOT_CONFIGURED,

    /** The integration is enabled but this plot is not linked to the provider yet. */
    NOT_LINKED
}
