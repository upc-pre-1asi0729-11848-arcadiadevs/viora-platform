package com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources;

import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ExecutionStatus;

import java.util.Date;

public record CertifyApplicationResource(
        Long treatmentPrescriptionId,
        Date applicationDate,
        String appliedArea,
        String executionStatus,
        String fieldNote
) {}
