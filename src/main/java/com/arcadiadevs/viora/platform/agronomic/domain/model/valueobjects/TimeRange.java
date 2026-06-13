package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import java.time.LocalDate;

/**
 * Time range value object.
 *
 * <p>
 * Represents predefined time ranges used to query agronomic statistics.
 * </p>
 */
public enum TimeRange {

    LAST_7_DAYS(7),
    LAST_30_DAYS(30),
    LAST_90_DAYS(90),
    LAST_180_DAYS(180),
    LAST_365_DAYS(365),
    /** Current campaign, modeled as a rolling one-year window until season dates are tracked. */
    CAMPAIGN(365);

    private final int days;

    TimeRange(int days) {
        this.days = days;
    }

    public DateRange toDateRange(LocalDate referenceDate) {
        if (referenceDate == null) {
            throw new IllegalArgumentException("Reference date is required.");
        }

        return new DateRange(
                referenceDate.minusDays(days - 1L),
                referenceDate
        );
    }

    public static TimeRange from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Time range is required.");
        }

        try {
            return TimeRange.valueOf(
                    value.trim()
                            .toUpperCase()
                            .replace("-", "_")
            );
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Invalid time range. Allowed values are: LAST_7_DAYS, LAST_30_DAYS, LAST_90_DAYS, LAST_180_DAYS, LAST_365_DAYS, CAMPAIGN."
            );
        }
    }
}