package com.arcadiadevs.viora.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Resource for changing a user's password.
 */
@Schema(
    name = "ChangePasswordRequest",
    description = "Request to change the user's password",
    example = "{\"currentPassword\": \"oldSecret123\", \"newPassword\": \"newSecret456\"}"
)
public record ChangePasswordResource(
    @Schema(description = "The current password of the user", requiredMode = Schema.RequiredMode.REQUIRED)
    String currentPassword,

    @Schema(description = "The new password for the user", requiredMode = Schema.RequiredMode.REQUIRED)
    String newPassword
) {
}
