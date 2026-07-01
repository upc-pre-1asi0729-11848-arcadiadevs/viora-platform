package com.arcadiadevs.viora.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Resource received to register a new IAM user.
 */
@Schema(
    name = "SignUpRequest",
    description = "User sign-up request with credentials and role",
    example = "{\"username\": \"john.doe\", \"password\": \"SecurePass123!\", \"role\": \"ROLE_GROWER\"}"
)
public record SignUpResource(
    @Schema(
        description = "Desired username",
        example = "john.doe",
        minLength = 3,
        maxLength = 50
    )
    String username,

    @Schema(
        description = "User password (minimum 8 characters)",
        example = "SecurePass123!",
        minLength = 8,
        maxLength = 255
    )
    String password,

    @Schema(
        description = "Role to assign to the user",
        example = "ROLE_GROWER"
    )
    String role
) {
}
