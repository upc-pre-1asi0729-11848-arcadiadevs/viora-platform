package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import java.time.LocalDate;

/**
 * Value object representing a date range.
 *
 * <p>
 * This value object is used to query agronomic statistics between two dates.
 * </p>
 *
 * @param startDate The start date.
 * @param endDate The end date.
 */
public record DateRange(LocalDate startDate, LocalDate endDate) {

    /**
     * Compact constructor for DateRange.
     * Validates that the date range is valid.
     *
     * @throws IllegalArgumentException if startDate or endDate is null, or if startDate is after endDate.
     */
    public DateRange {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Date range dates cannot be null");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
    }
}