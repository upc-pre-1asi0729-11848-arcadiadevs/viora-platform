package com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources;

/**
 * Producer-facing read model for the Interventions overview. Composes the accepted
 * assistance case with its downstream lifecycle (treatment prescription, execution
 * certification and outcome) into a single row with a derived status.
 */
public record InterventionSummaryResource(
        String code,
        Long interventionRequestId,
        String referenceCode,
        Long plotId,
        Long alertId,
        Long specialistId,
        Long serviceProposalId,
        Long treatmentPrescriptionId,
        Long interventionExecutionId,
        Long interventionOutcomeId,
        String status,
        String serviceTitle,
        Double amount,
        String currency
) {
}
