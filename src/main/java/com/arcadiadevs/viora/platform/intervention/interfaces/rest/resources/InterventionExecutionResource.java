package com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources;

import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ExecutionStatus;

import java.util.Date;

public record InterventionExecutionResource(
        Long id,
        Long treatmentPrescriptionId,
        Date applicationDate,
        String appliedArea,
        ExecutionStatus executionStatus,
        String fieldNote
) {}
