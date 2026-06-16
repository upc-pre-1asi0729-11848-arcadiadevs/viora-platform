package com.arcadiadevs.viora.platform.surveillance.domain.model.entities;

import com.arcadiadevs.viora.platform.shared.domain.model.entities.AuditableModel;
import com.arcadiadevs.viora.platform.surveillance.domain.model.aggregates.Alert;
import lombok.Getter;

/**
 * Local entity representing an event in the Alert's timeline.
 */
@Getter
public class AlertTimelineRecord extends AuditableModel {
    private String tag; // e.g. "Info", "Warning", "Medium", "High"
    private String title;
    private String description;

    protected AlertTimelineRecord() {
        // Required by JPA
    }

    public AlertTimelineRecord(String tag, String title, String description) {
        if (tag == null || tag.isBlank()) throw new IllegalArgumentException("Tag is required.");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title is required.");
        if (description == null || description.isBlank()) throw new IllegalArgumentException("Description is required.");
        
        this.tag = tag;
        this.title = title;
        this.description = description;
    }
}
