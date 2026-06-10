package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;

/**
 * Date range value object.
 */
@Getter
@EqualsAndHashCode
public class DateRange {

    private final LocalDate startDate;
    private final LocalDate endDate;

    public DateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("Start date is required.");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("End date is required.");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }

        this.startDate = startDate;
        this.endDate = endDate;
    }
}