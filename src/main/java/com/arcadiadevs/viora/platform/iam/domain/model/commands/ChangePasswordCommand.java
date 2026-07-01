package com.arcadiadevs.viora.platform.iam.domain.model.commands;

import com.arcadiadevs.viora.platform.iam.domain.model.valueobjects.Password;

/**
 * Command to change a user's password.
 *
 * @param userId the ID of the user whose password is to be changed
 * @param currentPassword the user's current raw password
 * @param newPassword the validated new password
 */
public record ChangePasswordCommand(Long userId, String currentPassword, Password newPassword) {
}
