package com.arcadiadevs.viora.platform.surveillance.domain.model.aggregates;

import com.arcadiadevs.viora.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.arcadiadevs.viora.platform.surveillance.domain.model.entities.AlertTimelineRecord;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.AlertId;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.AlertSeverity;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.AlertSource;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.AlertStatus;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.ThreatType;
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

    public Alert markAsReviewed() {
        if (this.status != AlertStatus.UNDER_REVIEW) {
            this.status = AlertStatus.UNDER_REVIEW;
            addTimelineRecord("Info", "Alert marked as reviewed", "A specialist has acknowledged and is reviewing this alert.");
        }
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
