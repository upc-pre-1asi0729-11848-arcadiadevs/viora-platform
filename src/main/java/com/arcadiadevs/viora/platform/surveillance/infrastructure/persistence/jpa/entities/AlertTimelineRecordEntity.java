package com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.entities;

import com.arcadiadevs.viora.platform.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "alert_timeline_records")
@Getter
@NoArgsConstructor
public class AlertTimelineRecordEntity extends AuditableModel {

    private String tag;
    private String title;
    private String description;

    @ManyToOne
    @JoinColumn(name = "alert_id", nullable = false)
    private AlertEntity alert;

    public AlertTimelineRecordEntity(String tag, String title, String description, AlertEntity alert) {
        this.tag = tag;
        this.title = title;
        this.description = description;
        this.alert = alert;
    }
}
