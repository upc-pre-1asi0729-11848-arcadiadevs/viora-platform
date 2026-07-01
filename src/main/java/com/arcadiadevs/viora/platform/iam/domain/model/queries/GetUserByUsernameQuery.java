package com.arcadiadevs.viora.platform.iam.domain.model.queries;

import com.arcadiadevs.viora.platform.iam.domain.model.valueobjects.Username;

/**
 * Get user by username query
 * <p>
 *     This class represents the query to get a user by its username.
 * </p>
 * @param username the username of the user
 */
public record GetUserByUsernameQuery(Username username) {
}
