package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * PlotId value object.
 *
 * <P>
 *     Represents the unique identifies of a plot in the agronomic bounded context.
 * </P>
 */

@Getter
@EqualsAndHashCode
public class PlotId {

    /**
     * The raw numeric identifies
     */
    private final Long value;

    /**
     * Constructor for PlotId
     * @param value the Plot identifies.
     */
    public PlotId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Plot ID must be a positive number.");
        }
        this.value = value;
    }

}
