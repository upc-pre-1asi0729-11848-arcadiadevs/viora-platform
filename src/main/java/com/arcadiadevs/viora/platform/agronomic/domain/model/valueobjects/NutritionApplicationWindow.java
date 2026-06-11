package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;

/**
 * Represents the date window in which a nutrition plan should be applied in field.
 */
@Getter
@EqualsAndHashCode
public class NutritionApplicationWindow {

    private final LocalDate startDate;
    private final LocalDate endDate;

    public NutritionApplicationWindow(LocalDate startDate, LocalDate endDate) {
        validateRequiredFields(startDate, endDate);
        validateConsistency(startDate, endDate);

        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Indicates whether the given date falls within the application window.
     *
     * @param date The date to evaluate.
     * @return True when the date is inside the window (inclusive).
     */
    public boolean contains(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date is required.");
        }
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Indicates whether the window is already closed at the given date.
     *
     * @param date The date to evaluate.
     * @return True when the window ended before the date.
     */
    public boolean isExpiredOn(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date is required.");
        }
        return endDate.isBefore(date);
    }

    private void validateRequiredFields(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("Application window start date is required.");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("Application window end date is required.");
        }
    }

    private void validateConsistency(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Application window start date cannot be after end date.");
        }
    }
}
