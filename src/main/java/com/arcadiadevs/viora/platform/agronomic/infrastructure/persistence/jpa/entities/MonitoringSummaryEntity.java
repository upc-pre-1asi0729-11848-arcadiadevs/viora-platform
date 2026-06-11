package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.entities;

import com.arcadiadevs.viora.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * JPA entity for MonitoringSummary persistence.
 */
@Entity
@Table(
        name = "monitoring_summaries",
        indexes = {
                @Index(
                        name = "idx_monitoring_summaries_user_measurement_date",
                        columnList = "user_id, measurement_date",
                        unique = true
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class MonitoringSummaryEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "general_health_status", nullable = false)
    private String generalHealthStatus; // Storing as String for simplicity, could be custom converter

    @Column(name = "ndvi_value", nullable = false)
    private Double ndviValue;

    @Column(name = "accumulated_chill_hours", nullable = false)
    private Double accumulatedChillHours;

    @Column(name = "yield_forecast", nullable = false)
    private Double yieldForecast;

    @Column(name = "measurement_date", nullable = false)
    private LocalDate measurementDate;

    // New fields for WeatherSnapshot
    @Enumerated(EnumType.STRING)
    @Column(name = "weather_status", nullable = false)
    private String weatherStatus;

    @Column(name = "weather_measurement_date", nullable = false)
    private LocalDate weatherMeasurementDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "weather_climate_risk_level", nullable = false)
    private String weatherClimateRiskLevel;

    @Column(name = "weather_temperature", nullable = false) // New field for temperature
    private Double weatherTemperature;

    // New field for ClimateRiskLevel (direct from aggregate)
    @Enumerated(EnumType.STRING)
    @Column(name = "climate_risk_level", nullable = false)
    private String climateRiskLevel;

    // New fields for MitigationRecommendation (simplified: storing first recommendation's details)
    @Enumerated(EnumType.STRING)
    @Column(name = "mitigation_action_type")
    private String mitigationActionType;

    @Column(name = "nutrition_input_recommendation")
    private String nutritionInputRecommendation;

    @Column(name = "mitigation_application_window_start")
    private LocalDate mitigationApplicationWindowStart;

    @Column(name = "mitigation_application_window_end")
    private LocalDate mitigationApplicationWindowEnd;
}