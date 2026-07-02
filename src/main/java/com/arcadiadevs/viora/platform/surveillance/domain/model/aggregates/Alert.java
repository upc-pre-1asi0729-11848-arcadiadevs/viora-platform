package com.arcadiadevs.viora.platform.surveillance.domain.model.aggregates;

import com.arcadiadevs.viora.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.arcadiadevs.viora.platform.surveillance.domain.model.entities.AlertTimelineRecord;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.AlertId;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.AlertSeverity;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.AlertSource;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.AlertStatus;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.ThreatType;
import com.arcadiadevs.viora.platform.surveillance.domain.exceptions.AlertAlreadyReviewedException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Alert aggregate root.
 * 
 * <p>Represents an active or resolved threat detected in a plot. An alert is a living entity
 * that evolves over time, keeping track of its own historical timeline and supporting metrics.</p>
 */
@Getter
public class Alert extends AbstractDomainAggregateRoot<Alert> {
    
    private AlertId id;
    private PlotId plotId;

    /** Originating pest sighting report, when this alert was raised from a manual report. */
    private Long reportId;

    private ThreatType type;
    private AlertSeverity severity;
    private AlertStatus status;
    
    private String title;
    private String riskExplanation;
    
    private List<AlertSource> sources;
    private List<String> dataProviders; // e.g. "AgroMonitoring", "Viora model"
    
    private Map<String, String> supportingData; // Key-Value metrics e.g. "Current NDVI": "0.55"
    
    private List<AlertTimelineRecord> timeline;

    protected Alert() {
        this.sources = new ArrayList<>();
        this.dataProviders = new ArrayList<>();
        this.supportingData = new HashMap<>();
        this.timeline = new ArrayList<>();
    }

    public Alert(PlotId plotId, ThreatType type, AlertSeverity severity, String title, String riskExplanation) {
        this();
        if (plotId == null) throw new IllegalArgumentException("PlotId is required.");
        if (type == null) throw new IllegalArgumentException("ThreatType is required.");
        if (severity == null) throw new IllegalArgumentException("AlertSeverity is required.");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title is required.");
        if (riskExplanation == null || riskExplanation.isBlank()) throw new IllegalArgumentException("Risk explanation is required.");

        this.plotId = plotId;
        this.type = type;
        this.severity = severity;
        this.status = AlertStatus.ACTIVE;
        this.title = title;
        this.riskExplanation = riskExplanation;
        
        // Add initial timeline record
        addTimelineRecord("Info", "Alert generated", "The system generated this alert based on initial findings.");
    }
    
    public Alert addSource(AlertSource source) {
        if (!this.sources.contains(source)) {
            this.sources.add(source);
        }
        return this;
    }
    
    public Alert addDataProvider(String provider) {
        if (!this.dataProviders.contains(provider)) {
            this.dataProviders.add(provider);
        }
        return this;
    }
    
    public Alert addSupportingData(String key, String value) {
        this.supportingData.put(key, value);
        return this;
    }

    public Alert addTimelineRecord(String tag, String recordTitle, String recordDescription) {
        this.timeline.add(new AlertTimelineRecord(tag, recordTitle, recordDescription));
        return this;
    }

    public Alert escalate(AlertSeverity newSeverity, String reason) {
        if (newSeverity == null) throw new IllegalArgumentException("New severity is required.");
        if (this.severity != newSeverity) {
            this.severity = newSeverity;
            addTimelineRecord(newSeverity.name(), "Alert escalated to " + newSeverity.name().toLowerCase() + " severity", reason);
        }
        return this;
    }

    /** Links this alert to the pest sighting report that originated it. */
    public Alert linkReport(Long reportId) {
        this.reportId = reportId;
        return this;
    }

    /**
     * Confirms the alert after a grower's field inspection corroborated the threat:
     * escalates it to high priority and records the confirmation in the timeline.
     */
    public Alert confirmFromInspection() {
        this.severity = AlertSeverity.HIGH;
        this.status = AlertStatus.ACTIVE;
        addTimelineRecord(
                "High",
                "Threat confirmed after field inspection",
                "The grower inspected the plot and confirmed the threat. The alert was escalated to high priority.");
        return this;
    }

    /**
     * Dismisses the alert (e.g. a grower ruled it out as a verified false positive after
     * inspection). Dismissed alerts drop out of the active panel.
     */
    public Alert dismiss(String reason) {
        this.status = AlertStatus.DISMISSED;
        addTimelineRecord(
                "Info",
                "Alert dismissed",
                reason != null && !reason.isBlank() ? reason : "The alert was dismissed.");
        return this;
    }

    /**
     * Resolves the alert: the threat was addressed, whether by the producer directly
     * or through a closed technical intervention. Resolved alerts drop out of the
     * active panel.
     */
    public Alert resolve() {
        this.status = AlertStatus.RESOLVED;
        addTimelineRecord(
                "Info",
                "Alert resolved",
                "The threat was addressed and the alert was resolved.");
        return this;
    }

    public Alert markAsReviewed() {
        if (this.status == AlertStatus.UNDER_REVIEW || this.status == AlertStatus.RESOLVED || this.status == AlertStatus.DISMISSED) {
            throw new AlertAlreadyReviewedException(this.id.value());
        }
        this.status = AlertStatus.UNDER_REVIEW;
        addTimelineRecord("Info", "Alert marked as reviewed", "A specialist has acknowledged and is reviewing this alert.");
        return this;
    }

    public Alert restoreIdentity(AlertId id) {
        if (id == null) {
            throw new IllegalArgumentException("Alert ID is required.");
        }
        if (this.id != null && !this.id.equals(id)) {
            throw new IllegalStateException("Alert identity cannot be changed.");
        }
        this.id = id;
        return this;
    }
}
