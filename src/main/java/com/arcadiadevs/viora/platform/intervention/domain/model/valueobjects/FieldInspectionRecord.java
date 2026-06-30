package com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects;

import java.util.Date;

/**
 * Represents the findings of a field inspection by a specialist.
 *
 * @param findingType          the type of finding (e.g., Phytosanitary)
 * @param incidenceLevel       the severity level (e.g., Low, Medium, Critical)
 * @param technicalDescription the technical description of symptoms
 * @param recordDate           the date and time the record was captured
 */
public record FieldInspectionRecord(
        FindingType findingType,
        IncidenceLevel incidenceLevel,
        String technicalDescription,
        Date recordDate
) {
    public FieldInspectionRecord {
        if (findingType == null) {
            throw new IllegalArgumentException("Finding type cannot be null");
        }
        if (incidenceLevel == null) {
            throw new IllegalArgumentException("Incidence level cannot be null");
        }
        if (recordDate == null) {
            recordDate = new Date();
        }
    }
}
