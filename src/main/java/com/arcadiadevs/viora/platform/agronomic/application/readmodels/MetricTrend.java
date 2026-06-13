package com.arcadiadevs.viora.platform.agronomic.application.readmodels;

/**
 * A metric's value compared against the previous comparable period.
 *
 * @param currentValue Average over the current window, or null when no data exists.
 * @param previousValue Average over the previous window, or null when no data exists.
 * @param change Difference (current minus previous), or null when not computable.
 * @param changePercent Percentage change relative to the previous value, or null.
 * @param direction Classified direction of the change.
 */
public record MetricTrend(
        Double currentValue,
        Double previousValue,
        Double change,
        Double changePercent,
        TrendDirection direction
) {
    public static MetricTrend stableUnknown() {
        return new MetricTrend(null, null, null, null, TrendDirection.STABLE);
    }
}
