package com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "pest_sighting_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PestSightingReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long plotId;
    private Long reporterUserId;
    private String riskZone;
    private String symptoms; // JSON string or comma separated
    private String observedSeverity;
    private String notes;
    private boolean evaluated;
    private String calculatedRisk;
    private String probableThreat;
    private String status;
    private boolean alertConfirmed;
}
