package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

/**
 * Value object representing the user id.
 *
 * <p>
 * This value object is used to link agronomic data to a user.
 * It must be a positive Long value.
 * </p>
 *
 * @param userId The user id. It cannot be null or less than 1.
 */
public record UserId(Long userId) {

    /**
     * Compact constructor for UserId.
     * Validates that the userId is not null and is greater than or equal to 1.
     *
     * @throws IllegalArgumentException if the userId is null or less than 1.
     */
    public UserId {
        if (userId == null || userId < 1) {
            throw new IllegalArgumentException("User id cannot be null or less than 1");
        }
    }
}