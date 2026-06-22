package com.arcadiadevs.viora.platform.surveillance.domain.model.aggregates;

import com.arcadiadevs.viora.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.AlertSeverity;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.PestSightingReportId;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.ReporterUserId;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.RiskZone;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.Symptoms;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.ThreatType;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.ReportStatus;
import lombok.Getter;

import java.util.EnumSet;
import java.util.Set;

/**
 * Pest Sighting Report aggregate root.
 *
 * <p>
 *     Acts as the entry funnel for pest or disease detections, whether manual
 *     (reported by a grower) or autonomous (detected by remote sensing).
 * </p>
 */
@Getter
public class PestSightingReport extends AbstractDomainAggregateRoot<PestSightingReport> {

    private static final int NOTES_MAX_LENGTH = 1000;

    /**
     * NDVI below this value indicates canopy stress severe enough to corroborate
     * field-reported damage. It raises confidence; it never gates a report on its own.
     */
    private static final double NDVI_DAMAGE_THRESHOLD = 0.40;

    /**
     * Quarantine pathogens that must be escalated on any credible suspicion,
     * regardless of the reporter's severity or current NDVI (e.g. Xylella fastidiosa,
     * an EU-regulated quarantine organism).
     */
    private static final Set<ThreatType> QUARANTINE_THREATS = EnumSet.of(ThreatType.XYLELLA_RELATED);

    private PestSightingReportId id;
    private PlotId plotId;
    private ReporterUserId reporterUserId;
    private RiskZone riskZone;
    private Symptoms symptoms;
    private AlertSeverity observedSeverity;
    private String notes;
    
    private boolean evaluated;
    private AlertSeverity calculatedRisk;
    private ThreatType probableThreat;
    private ReportStatus status;
    private boolean alertConfirmed;

    protected PestSightingReport() {
        this.evaluated = false;
        this.notes = "";
        this.status = ReportStatus.LOGGED;
        this.alertConfirmed = false;
    }

    public PestSightingReport(
            PlotId plotId,
            ReporterUserId reporterUserId,
            RiskZone riskZone,
            Symptoms symptoms,
            AlertSeverity observedSeverity,
            String notes
    ) {
        validateRequiredFields(plotId, reporterUserId, riskZone, symptoms, observedSeverity);
        this.plotId = plotId;
        this.reporterUserId = reporterUserId;
        this.riskZone = riskZone;
        this.symptoms = symptoms;
        this.observedSeverity = observedSeverity;
        this.notes = sanitizeText(notes, NOTES_MAX_LENGTH, "Notes");
        this.evaluated = false;
        this.status = ReportStatus.LOGGED;
        this.alertConfirmed = false;
    }

    /**
     * Registers a new manual pest sighting report.
     *
     * @param plotId The affected plot identifier.
     * @param reporterUserId The user reporting the sighting.
     * @param riskZone The affected zone within the plot.
     * @param symptoms The observed symptoms.
     * @param observedSeverity The severity observed by the reporter.
     * @param notes Additional free-form notes.
     * @return A new PestSightingReport.
     */
    public static PestSightingReport registerManualReport(
            PlotId plotId,
            ReporterUserId reporterUserId,
            RiskZone riskZone,
            Symptoms symptoms,
            AlertSeverity observedSeverity,
            String notes
    ) {
        return new PestSightingReport(
                plotId,
                reporterUserId,
                riskZone,
                symptoms,
                observedSeverity,
                notes
        );
    }

    /**
     * Triages the report by combining the grower's observed severity (subjective field
     * signal), the plot's current satellite NDVI (objective but lagging signal), and the
     * inferred threat. NDVI raises confidence; it never gates a report on its own. Every
     * report ends in a coherent terminal state — there is no passive "review" limbo.
     *
     * @param currentNdvi The current NDVI value fetched from the Agronomic context (nullable).
     * @param inferredThreat The threat inferred by the domain service.
     */
    public PestSightingReport evaluateBiologicalRisk(Double currentNdvi, ThreatType inferredThreat) {
        boolean ndviConfirmsDamage = (currentNdvi != null && currentNdvi < NDVI_DAMAGE_THRESHOLD);
        boolean quarantineThreat = inferredThreat != null && QUARANTINE_THREATS.contains(inferredThreat);

        this.probableThreat = inferredThreat;
        this.evaluated = true;

        // Quarantine pathogens are escalated on any credible suspicion, regardless of
        // the reporter's severity or current NDVI.
        if (quarantineThreat) {
            this.status = ReportStatus.CONFIRMED;
            this.alertConfirmed = true;
            this.calculatedRisk = AlertSeverity.CRITICAL;
            return this;
        }

        // CONFIRMED: subjective and objective signals agree (or self-reported as critical).
        if (this.observedSeverity == AlertSeverity.CRITICAL
                || (this.observedSeverity == AlertSeverity.HIGH && ndviConfirmsDamage)) {
            this.status = ReportStatus.CONFIRMED;
            this.alertConfirmed = true;
            this.calculatedRisk = AlertSeverity.HIGH;
            return this;
        }

        // NEEDS_INSPECTION: a real signal exists but is not yet corroborated.
        if (this.observedSeverity == AlertSeverity.HIGH
                || (this.observedSeverity == AlertSeverity.MEDIUM && ndviConfirmsDamage)) {
            this.status = ReportStatus.NEEDS_INSPECTION;
            this.alertConfirmed = false;
            this.calculatedRisk = AlertSeverity.MEDIUM;
            return this;
        }

        // LOGGED: weak signal — recorded for community epidemiology, no alert raised.
        this.status = ReportStatus.LOGGED;
        this.alertConfirmed = false;
        this.calculatedRisk = AlertSeverity.LOW;
        return this;
    }

    /**
     * Confirms the report after a field inspection corroborated the threat. Only valid for a
     * report awaiting inspection; escalates it so a high-priority alert is raised.
     */
    public PestSightingReport confirmAfterInspection() {
        requireAwaitingInspection();
        this.status = ReportStatus.CONFIRMED;
        this.alertConfirmed = true;
        this.calculatedRisk = AlertSeverity.HIGH;
        return this;
    }

    /**
     * Rules out the report after a field inspection found no real threat (verified false
     * positive). Only valid for a report awaiting inspection; no alert is raised.
     */
    public PestSightingReport dismissAfterInspection() {
        requireAwaitingInspection();
        this.status = ReportStatus.RULED_OUT;
        this.alertConfirmed = false;
        this.calculatedRisk = AlertSeverity.LOW;
        return this;
    }

    /** A report can only be resolved by inspection while it is awaiting one. */
    private void requireAwaitingInspection() {
        if (this.status != ReportStatus.NEEDS_INSPECTION && this.status != ReportStatus.UNDER_REVIEW) {
            throw new IllegalStateException(
                    "Only a report awaiting inspection can be confirmed or ruled out.");
        }
    }

    public PestSightingReport restoreEvaluationState(AlertSeverity calculatedRisk, ThreatType probableThreat, ReportStatus status, boolean alertConfirmed) {
        this.evaluated = true;
        this.calculatedRisk = calculatedRisk;
        this.probableThreat = probableThreat;
        this.status = status;
        this.alertConfirmed = alertConfirmed;
        return this;
    }

    public PestSightingReport restoreIdentity(PestSightingReportId id) {
        if (id == null) {
            throw new IllegalArgumentException("Report ID is required.");
        }
        if (this.id != null && !this.id.equals(id)) {
            throw new IllegalStateException("Report identity cannot be changed.");
        }
        this.id = id;
        return this;
    }

    private void validateRequiredFields(
            PlotId plotId,
            ReporterUserId reporterUserId,
            RiskZone riskZone,
            Symptoms symptoms,
            AlertSeverity observedSeverity
    ) {
        if (plotId == null) throw new IllegalArgumentException("Plot ID is required.");
        if (reporterUserId == null) throw new IllegalArgumentException("Reporter User ID is required.");
        if (riskZone == null) throw new IllegalArgumentException("Risk zone is required.");
        if (symptoms == null) throw new IllegalArgumentException("Symptoms are required.");
        if (observedSeverity == null) throw new IllegalArgumentException("Observed severity is required.");
    }

    private String sanitizeText(String value, int maxLength, String fieldName) {
        var sanitizedValue = value == null ? "" : value.trim();
        if (sanitizedValue.length() > maxLength) {
            throw new IllegalArgumentException(
                    "%s cannot exceed %d characters.".formatted(fieldName, maxLength)
            );
        }
        return sanitizedValue;
    }
}
