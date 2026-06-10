package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

/**
 * Value object representing the authenticated user id.
 *
 * <p>
 * This value object is used to validate if the authenticated user
 * is allowed to access the requested agronomic statistics.
 * </p>
 *
 * @param authenticatedUserId The authenticated user id. It cannot be null or less than 1.
 */
public record AuthenticatedUserId(Long authenticatedUserId) {

    /**
     * Compact constructor for AuthenticatedUserId.
     * Validates that the authenticatedUserId is not null and is greater than or equal to 1.
     *
     * @throws IllegalArgumentException if the authenticatedUserId is null or less than 1.
     */
    public AuthenticatedUserId {
        if (authenticatedUserId == null || authenticatedUserId < 1) {
            throw new IllegalArgumentException("Authenticated user id cannot be null or less than 1");
        }
    }
}