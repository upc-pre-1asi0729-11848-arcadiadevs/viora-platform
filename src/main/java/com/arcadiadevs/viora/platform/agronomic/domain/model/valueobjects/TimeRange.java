package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import java.time.LocalDate;

/**
 * Enumeration representing the supported time ranges for agronomic statistics.
 *
 * <p>
 * This enumeration is used to transform a selected time range into a date range.
 * </p>
 */
public enum TimeRange {
    /**
     * Last 7 days.
     */
    LAST_7_DAYS,

    /**
     * Last 30 days.
     */
    LAST_30_DAYS,

    /**
     * Last 90 days.
     */
    LAST_90_DAYS,

    /**
     * Last 12 months.
     */
    LAST_12_MONTHS;

    /**
     * Converts the time range into a DateRange.
     *
     * @return The equivalent DateRange.
     */
    public DateRange toDateRange() {
        var endDate = LocalDate.now();

        return switch (this) {
            case LAST_7_DAYS -> new DateRange(endDate.minusDays(7), endDate);
            case LAST_30_DAYS -> new DateRange(endDate.minusDays(30), endDate);
            case LAST_90_DAYS -> new DateRange(endDate.minusDays(90), endDate);
            case LAST_12_MONTHS -> new DateRange(endDate.minusMonths(12), endDate);
        };
    }

    /**
     * Creates a TimeRange from a string value.
     *
     * @param value The string value.
     * @return The TimeRange.
     */
    public static TimeRange from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Time range cannot be null or empty");
        }

        try {
            return TimeRange.valueOf(value.trim().toUpperCase().replace("-", "_"));
        } catch (Exception exception) {
            throw new IllegalArgumentException("Unsupported time range");
        }
    }
}