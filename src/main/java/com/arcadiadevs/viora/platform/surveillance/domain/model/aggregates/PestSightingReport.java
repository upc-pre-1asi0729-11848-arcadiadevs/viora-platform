package com.arcadiadevs.viora.platform.surveillance.domain.model.aggregates;

import com.arcadiadevs.viora.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.AlertSeverity;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.PestSightingReportId;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.ReporterUserId;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.RiskZone;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.Symptoms;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.ThreatType;
import lombok.Getter;

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

    protected PestSightingReport() {
        this.evaluated = false;
        this.notes = "";
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
     * Evaluates the biological risk of the report, setting the calculated risk and probable threat.
     */
    public PestSightingReport evaluateBiologicalRisk(AlertSeverity calculatedRisk, ThreatType probableThreat) {
        if (calculatedRisk == null) throw new IllegalArgumentException("Calculated risk is required");
        if (probableThreat == null) throw new IllegalArgumentException("Probable threat is required");

        this.calculatedRisk = calculatedRisk;
        this.probableThreat = probableThreat;
        this.evaluated = true;
        
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
