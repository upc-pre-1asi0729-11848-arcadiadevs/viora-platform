package com.arcadiadevs.viora.platform.agronomic.infrastructure.external.agromonitoring;

import com.arcadiadevs.viora.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Persistence state required to correlate a Viora plot with AgroMonitoring.
 */
@Entity
@Table(
        name = "agro_monitoring_plot_integrations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_agro_monitoring_plot_integrations_plot",
                        columnNames = "plot_id"
                ),
                @UniqueConstraint(
                        name = "uk_agro_monitoring_plot_integrations_external_polygon",
                        columnNames = "external_polygon_id"
                )
        },
        indexes = @Index(
                name = "idx_agro_monitoring_plot_integrations_plot",
                columnList = "plot_id"
        )
)
@Getter
@Setter
@NoArgsConstructor
public class AgroMonitoringPlotIntegrationEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "plot_id", nullable = false)
    private Long plotId;

    @Column(name = "external_polygon_id", nullable = false, length = 80)
    private String externalPolygonId;

    @Column(name = "boundary_fingerprint", nullable = false, length = 64)
    private String boundaryFingerprint;

    @Column(name = "provider_imagery_id", length = 80)
    private String providerImageryId;

    @Column(name = "tile_url", length = 500)
    private String tileUrl;

    @Column(name = "capture_date")
    private Instant captureDate;

    @Column(name = "ndvi_mean")
    private Double ndviMean;

    @Column(name = "cloud_percentage")
    private Double cloudPercentage;

    @Column(name = "last_checked_at")
    private Instant lastCheckedAt;
}
