package com.arcadiadevs.viora.platform.surveillance.domain.exceptions;

import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.PestSightingReportId;

/**
 * Exception thrown when a pest sighting report is not found.
 */
public class PestSightingReportNotFoundException extends RuntimeException {
    public PestSightingReportNotFoundException(PestSightingReportId reportId) {
        super("Pest sighting report with ID " + reportId.value() + " was not found.");
    }
}
