package com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources;

import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.FindingType;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.IncidenceLevel;

import java.util.Date;

public record LogFieldInspectionDataResource(
        FindingType findingType,
        IncidenceLevel incidenceLevel,
        String technicalDescription,
        Date recordDate
) {}
