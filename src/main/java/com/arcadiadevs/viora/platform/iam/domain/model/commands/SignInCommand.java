package com.arcadiadevs.viora.platform.iam.domain.model.commands;

import com.arcadiadevs.viora.platform.iam.domain.model.valueobjects.Password;
import com.arcadiadevs.viora.platform.iam.domain.model.valueobjects.Username;

/**
 * Sign in command
 * <p>
 *     This class represents the command to sign in a user.
 * </p>
 * @param username the username of the user
 * @param password the password of the user
 *
 */
public record SignInCommand(Username username, Password password) {
}
