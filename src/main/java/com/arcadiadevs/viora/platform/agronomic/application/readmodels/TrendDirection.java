package com.arcadiadevs.viora.platform.agronomic.application.readmodels;

/**
 * Direction of a metric's change relative to the previous comparable period.
 */
public enum TrendDirection {

    /** The metric increased beyond the stability margin. */
    UP,

    /** The metric decreased beyond the stability margin. */
    DOWN,

    /** The metric is essentially unchanged. */
    STABLE
}
