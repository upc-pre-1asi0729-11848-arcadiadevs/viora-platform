package com.arcadiadevs.viora.platform.surveillance.domain.model.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Domain event representing that a pest sighting report has been evaluated
 * and its biological risk and probable threat have been calculated.
 *
 * <p>
 * This event is published when a manual report is successfully saved and its initial
 * threat severity has been automatically assigned.
 * </p>
 */
@Getter
public class PestSightingReportEvaluatedEvent extends ApplicationEvent {

    /**
     * The unique identifier of the generated report.
     */
    private final Long reportId;

    /**
     * The identifier of the affected plot.
     */
    private final Long plotId;

    /**
     * The identifier of the user who reported the sighting.
     */
    private final Long reporterUserId;

    /**
     * The automatically calculated biological risk severity.
     */
    private final String calculatedRisk;

    /**
     * The probable threat identified during evaluation.
     */
    private final String probableThreat;

    /**
     * True if the threat was confirmed and a high-priority alert should be created.
     */
    private final boolean alertConfirmed;

    /**
     * The triage outcome ({@code LOGGED}, {@code NEEDS_INSPECTION}, {@code CONFIRMED}),
     * used to decide whether and what kind of alert to raise.
     */
    private final String status;

    /**
     * Constructor for PestSightingReportEvaluatedEvent.
     *
     * @param source         The object that published the event (usually the aggregate or service).
     * @param reportId       The unique identifier of the report.
     * @param plotId         The identifier of the affected plot.
     * @param reporterUserId The identifier of the user who reported the sighting.
     * @param calculatedRisk The calculated biological risk severity.
     * @param probableThreat The probable threat identified.
     * @param alertConfirmed Whether a high-priority alert should be generated.
     * @param status         The triage outcome of the report.
     */
    public PestSightingReportEvaluatedEvent(
            Object source,
            Long reportId,
            Long plotId,
            Long reporterUserId,
            String calculatedRisk,
            String probableThreat,
            boolean alertConfirmed,
            String status
    ) {
        super(source);
        this.reportId = reportId;
        this.plotId = plotId;
        this.reporterUserId = reporterUserId;
        this.calculatedRisk = calculatedRisk;
        this.probableThreat = probableThreat;
        this.alertConfirmed = alertConfirmed;
        this.status = status;
    }
}
