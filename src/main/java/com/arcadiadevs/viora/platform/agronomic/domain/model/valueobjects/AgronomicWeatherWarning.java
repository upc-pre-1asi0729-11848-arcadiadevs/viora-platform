package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import java.time.LocalDate;

/**
 * An agronomic warning derived from forecast conditions on a given day.
 *
 * @param type The warning category.
 * @param severity The climate risk level implied by the condition.
 * @param date The forecast day the warning applies to.
 * @param message Human-readable description.
 */
public record AgronomicWeatherWarning(
        WeatherWarningType type,
        ClimateRiskLevel severity,
        LocalDate date,
        String message
) {
    public AgronomicWeatherWarning {
        if (type == null) {
            throw new IllegalArgumentException("Weather warning type is required.");
        }
        if (severity == null) {
            throw new IllegalArgumentException("Weather warning severity is required.");
        }
        if (date == null) {
            throw new IllegalArgumentException("Weather warning date is required.");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Weather warning message is required.");
        }
    }
}
