package com.arcadiadevs.viora.platform.agronomic.domain.model.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Domain event representing the registration of a new agricultural plot.
 *
 * <p>
 * This event is published when a productive plot is successfully created 
 * and persistent within the agronomic domain.
 * </p>
 */
@Getter
public class PlotRegisteredEvent extends ApplicationEvent {

    /**
     * The unique identifier of the registered plot.
     */
    private final Long plotId;

    /**
     * The identifier of the owner user.
     */
    private final Long userId;

    /**
     * Constructor for PlotRegisteredEvent.
     *
     * @param source   The object that published the event (usually the aggregate or service).
     * @param plotId   The unique identifier of the plot.
     * @param userId   The identifier of the owner user.
     */
    public PlotRegisteredEvent(Object source, Long plotId, Long userId) {
        super(source);
        this.plotId = plotId;
        this.userId = userId;
    }
}