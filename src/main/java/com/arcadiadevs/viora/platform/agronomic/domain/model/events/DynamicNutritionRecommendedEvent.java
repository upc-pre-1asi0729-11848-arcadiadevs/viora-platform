package com.arcadiadevs.viora.platform.agronomic.domain.model.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Domain event representing the recommendation of a new dynamic nutrition plan.
 *
 * <p>
 * This event is published when a dynamic nutrition plan is generated for a plot
 * in response to a significant climate risk within the agronomic domain.
 * </p>
 */
@Getter
public class DynamicNutritionRecommendedEvent extends ApplicationEvent {

    /**
     * The identifier of the plot the plan was generated for.
     */
    private final Long plotId;

    /**
     * The identifier of the owner user.
     */
    private final Long userId;

    /**
     * The climate risk level that triggered the recommendation.
     */
    private final String triggeringRiskLevel;

    /**
     * Constructor for DynamicNutritionRecommendedEvent.
     *
     * @param source The object that published the event (usually the aggregate).
     * @param plotId The identifier of the plot.
     * @param userId The identifier of the owner user.
     * @param triggeringRiskLevel The climate risk level that triggered the plan.
     */
    public DynamicNutritionRecommendedEvent(
            Object source,
            Long plotId,
            Long userId,
            String triggeringRiskLevel
    ) {
        super(source);
        this.plotId = plotId;
        this.userId = userId;
        this.triggeringRiskLevel = triggeringRiskLevel;
    }
}
