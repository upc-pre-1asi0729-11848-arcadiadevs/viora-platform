package com.arcadiadevs.viora.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Resource returned after successful authentication.
 *
 * <p>Contains the authenticated user identifier, username, and the bearer token to be used in
 * subsequent API calls.</p>
 */
@Schema(
    name = "AuthenticatedUserResponse",
    description = "Authenticated user information with JWT token",
    example = "{\"id\": 1, \"username\": \"john.doe\", \"token\": \"eyJhbGciOiJIUzI1NiIs...\", \"role\": \"ROLE_GROWER\"}"
)
public record AuthenticatedUserResource(
    @Schema(description = "User unique identifier", example = "1")
    Long id,

    @Schema(description = "User username", example = "john.doe")
    String username,

    String token,

    @Schema(description = "User assigned role", example = "ROLE_GROWER")
    String role
) {
}
