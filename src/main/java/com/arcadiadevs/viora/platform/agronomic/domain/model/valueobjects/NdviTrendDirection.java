package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

/**
 * Direction of an NDVI trend over a time window.
 */
public enum NdviTrendDirection {

    /** Vegetation vigor is increasing. */
    RISING,

    /** Vegetation vigor is decreasing. */
    FALLING,

    /** Vegetation vigor is essentially unchanged. */
    STABLE
}
