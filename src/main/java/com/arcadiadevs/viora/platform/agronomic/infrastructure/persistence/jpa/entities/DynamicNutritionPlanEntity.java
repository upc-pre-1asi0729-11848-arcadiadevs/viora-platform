package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.entities;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NutritionInputRecommendation;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NutritionPlanStatus;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.converters.NutritionInputRecommendationAttributeConverter;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.converters.NutritionPlanStatusAttributeConverter;
import com.arcadiadevs.viora.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * JPA entity for DynamicNutritionPlan persistence.
 */
@Entity
@Table(
        name = "dynamic_nutrition_plans",
        indexes = {
                @Index(
                        name = "idx_dynamic_nutrition_plans_user_plot_status",
                        columnList = "user_id, plot_id, status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class DynamicNutritionPlanEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "plot_id", nullable = false)
    private Long plotId;

    @Convert(converter = NutritionPlanStatusAttributeConverter.class)
    @Column(name = "status", nullable = false)
    private NutritionPlanStatus status;

    @Convert(converter = NutritionInputRecommendationAttributeConverter.class)
    @Column(name = "input_recommendations", nullable = false, length = 2000)
    private List<NutritionInputRecommendation> inputRecommendations;

    @Column(name = "application_window_start", nullable = false)
    private LocalDate applicationWindowStart;

    @Column(name = "application_window_end", nullable = false)
    private LocalDate applicationWindowEnd;

    // PlanRationale fields are flattened; reconstruction is handled by assemblers.
    @Column(name = "rationale_summary", nullable = false, length = 500)
    private String rationaleSummary;

    @Column(name = "rationale_risk_level", nullable = false)
    private String rationaleRiskLevel;

    @Column(name = "rationale_ndvi_value", nullable = false)
    private Double rationaleNdviValue;

    @Column(name = "rationale_temperature_anomaly", nullable = false)
    private Double rationaleTemperatureAnomaly;

    @Column(name = "generated_date", nullable = false)
    private LocalDate generatedDate;
}
