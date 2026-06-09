package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.entities;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PolygonCoordinates;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.converters.PolygonCoordinatesAttributeConverter;
import com.arcadiadevs.viora.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * JPA persistence entity for plots.
 *
 * <p>
 * Represents the database structure for productive agricultural plots in Viora.
 * This entity belongs to the infrastructure layer and extends the shared auditable
 * persistence entity to reuse id, createdAt and updatedAt fields.
 * </p>
 */
@Entity
@Table(
        name = "plots",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_plots_user_name",
                columnNames = {"user_id", "name"}
        ),
        indexes = @Index(
                name = "idx_plots_user_active",
                columnList = "user_id, active"
        )
)
@Getter
@Setter
@NoArgsConstructor
public class PlotPersistenceEntity extends AuditableAbstractPersistenceEntity {

    /**
     * The ID of the user who owns the plot.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * The plot name assigned by the grower.
     */
    @Column(nullable = false, length = 80)
    private String name;

    /**
     * The geographic polygon that delimits the plot.
     */
    @Convert(converter = PolygonCoordinatesAttributeConverter.class)
    @Column(name = "polygon_coordinates", nullable = false, columnDefinition = "TEXT")
    private PolygonCoordinates polygonCoordinates;

    /**
     * The productive area size in hectares.
     */
    @Column(name = "area_size_hectares", nullable = false, precision = 10, scale = 2)
    private BigDecimal areaSize;

    /**
     * The crop type associated with the plot.
     */
    @Column(name = "crop_type", length = 60)
    private String cropType;

    /**
     * The crop variety associated with the plot.
     */
    @Column(length = 80)
    private String variety;

    /**
     * Indicates whether the plot is active.
     */
    @Column(nullable = false)
    private Boolean active = true;
}
