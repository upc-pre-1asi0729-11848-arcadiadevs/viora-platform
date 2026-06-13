package com.arcadiadevs.viora.platform.agronomic.domain.model.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDate;

/**
 * Domain event published when a dynamic nutrition plan application is certified.
 *
 * <p>
 * Marks an executed agronomic action: it updates the plot history and is the
 * integration point for downstream contexts (e.g. expense declaration) and the
 * predictive engine. Consumers correlate by plot and plan.
 * </p>
 */
@Getter
public class NutritionApplicationCertifiedEvent extends ApplicationEvent {

    private final Long plotId;
    private final Long userId;
    private final LocalDate applicationDate;

    public NutritionApplicationCertifiedEvent(
            Object source,
            Long plotId,
            Long userId,
            LocalDate applicationDate
    ) {
        super(source);
        this.plotId = plotId;
        this.userId = userId;
        this.applicationDate = applicationDate;
    }
}
