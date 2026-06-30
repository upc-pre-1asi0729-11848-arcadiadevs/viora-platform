package com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embeddable representation of a prescribed product for JPA persistence.
 */
@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrescribedProductItem {
    /**
     * The name of the agrochemical product.
     */
    @Column(name = "product_name", nullable = false)
    private String productName;

    /**
     * The dosage amount (e.g., 2.5).
     */
    @Column(name = "dosage_amount", nullable = false)
    private Double dosageAmount;

    /**
     * The dosage unit (e.g., L/ha).
     */
    @Column(name = "dosage_unit", nullable = false, length = 20)
    private String dosageUnit;

    /**
     * The total number of application sessions for this product.
     */
    @Column(name = "sessions_count", nullable = false)
    private Integer sessionsCount;

    /**
     * Technical recommendations provided by the specialist for this product.
     */
    @Column(name = "technical_recommendation", columnDefinition = "TEXT")
    private String technicalRecommendation;
}
