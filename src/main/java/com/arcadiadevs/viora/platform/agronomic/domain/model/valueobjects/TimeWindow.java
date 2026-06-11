package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;

/**
 * Represents a time window with a start and end date.
 */
@Getter
@EqualsAndHashCode
public class TimeWindow {

    private final LocalDate startDate;
    private final LocalDate endDate;

    public TimeWindow(LocalDate startDate, LocalDate endDate) {
        validateRequiredFields(startDate, endDate);
        validateConsistency(startDate, endDate);
        
        this.startDate = startDate;
        this.endDate = endDate;
    }

    private void validateRequiredFields(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("Start date is required.");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("End date is required.");
        }
    }

    private void validateConsistency(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }
    }
}
