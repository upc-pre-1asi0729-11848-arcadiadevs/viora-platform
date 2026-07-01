package com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects;

import java.util.Date;

/**
 * Represents the date when the application was certified.
 *
 * @param date the date of application
 */
public record ApplicationDate(Date date) {
    public ApplicationDate {
        if (date == null) {
            throw new IllegalArgumentException("Application date cannot be null");
        }
        if (date.after(new Date())) {
            throw new IllegalArgumentException("Application date cannot be in the future");
        }
    }
}
