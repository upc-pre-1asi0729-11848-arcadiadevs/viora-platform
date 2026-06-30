package com.arcadiadevs.viora.platform.intervention.domain.model.commands;

import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.FindingType;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.IncidenceLevel;

import java.util.Date;

/**
 * Command to log field inspection data into a Treatment Prescription.
 *
 * @param treatmentPrescriptionId the ID of the treatment prescription
 * @param findingType             the type of finding
 * @param incidenceLevel          the severity
 * @param technicalDescription    description of the findings
 * @param recordDate              the date of the inspection
 */
public record LogFieldInspectionDataCommand(
        Long treatmentPrescriptionId,
        FindingType findingType,
        IncidenceLevel incidenceLevel,
        String technicalDescription,
        Date recordDate
) {
    public LogFieldInspectionDataCommand {
        if (treatmentPrescriptionId == null || treatmentPrescriptionId <= 0) {
            throw new IllegalArgumentException("Treatment prescription ID must be provided and positive");
        }
        if (findingType == null) {
            throw new IllegalArgumentException("Finding type cannot be null");
        }
        if (incidenceLevel == null) {
            throw new IllegalArgumentException("Incidence level cannot be null");
        }
    }
}
