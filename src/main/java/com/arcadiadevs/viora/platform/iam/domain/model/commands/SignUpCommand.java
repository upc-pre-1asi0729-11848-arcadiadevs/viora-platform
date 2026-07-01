package com.arcadiadevs.viora.platform.iam.domain.model.commands;

import com.arcadiadevs.viora.platform.iam.domain.model.entities.Role;

import com.arcadiadevs.viora.platform.iam.domain.model.valueobjects.Password;
import com.arcadiadevs.viora.platform.iam.domain.model.valueobjects.Username;

/**
 * Sign up command
 * <p>
 *     This class represents the command to sign up a user.
 * </p>
 * @param username the username of the user
 * @param password the password of the user
 * @param role the role of the user
 *
 * @see com.arcadiadevs.viora.platform.iam.domain.model.aggregates.User
 */
public record SignUpCommand(Username username, Password password, Role role) {
}
