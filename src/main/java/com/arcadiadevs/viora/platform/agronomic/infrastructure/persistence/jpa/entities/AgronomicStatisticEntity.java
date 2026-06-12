package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.entities;

import com.arcadiadevs.viora.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * JPA entity for AgronomicStatistic persistence.
 */
@Entity
@Table(
        name = "agronomic_statistics",
        indexes = {
                @Index(
                        name = "idx_agronomic_statistics_user_measurement_date",
                        columnList = "user_id, measurement_date"
                ),
                @Index(
                        name = "idx_agronomic_statistics_user_plot_measurement_date",
                        columnList = "user_id, plot_id, measurement_date"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_agronomic_statistics_plot_measurement_date",
                        columnNames = {"plot_id", "measurement_date"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class AgronomicStatisticEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "plot_id", nullable = false)
    private Long plotId;

    @Column(name = "measurement_date", nullable = false)
    private LocalDate measurementDate;

    @Column(name = "ndvi_value", nullable = false)
    private Double ndviValue;

    @Column(name = "chill_portions", nullable = false)
    private Double chillPortions;

    @Column(name = "chill_hours", nullable = false)
    private Double chillHours;
}