package com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.entities;

import com.arcadiadevs.viora.platform.shared.infrastructure.persistence.jpa.entities.AuditableEntity;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.AlertSeverity;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.AlertSource;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.AlertStatus;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.ThreatType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "alerts")
@Getter
@Setter
@NoArgsConstructor
public class AlertEntity extends AuditableEntity {

    private Long plotId;

    @Enumerated(EnumType.STRING)
    private ThreatType type;

    @Enumerated(EnumType.STRING)
    private AlertSeverity severity;

    @Enumerated(EnumType.STRING)
    private AlertStatus status;

    private String title;
    
    @Column(length = 2000)
    private String riskExplanation;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "alert_sources", joinColumns = @JoinColumn(name = "alert_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "source")
    private List<AlertSource> sources = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "alert_data_providers", joinColumns = @JoinColumn(name = "alert_id"))
    @Column(name = "provider")
    private List<String> dataProviders = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "alert_supporting_data", joinColumns = @JoinColumn(name = "alert_id"))
    @MapKeyColumn(name = "data_key")
    @Column(name = "data_value")
    private Map<String, String> supportingData = new HashMap<>();

    @OneToMany(mappedBy = "alert", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<AlertTimelineRecordEntity> timeline = new ArrayList<>();

    public void addTimelineRecord(AlertTimelineRecordEntity record) {
        timeline.add(record);
    }
}
