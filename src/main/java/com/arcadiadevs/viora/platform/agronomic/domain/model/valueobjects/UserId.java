package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * UserId value object.
 *
 * <p>
 *     represent the identifier of the user who owns a plot.
 * </p>
 */
@Getter
@EqualsAndHashCode
public class UserId {

    /**
     * The raw numeric identifier.
     */
    private final Long value;

    /**
     * Constructor for UserId
     * @param value The user identifies
     */
    public UserId(Long value) {
        if (value == null)
            throw new IllegalArgumentException("User ID must be a positive number.");
        this.value = value;
    }

}
