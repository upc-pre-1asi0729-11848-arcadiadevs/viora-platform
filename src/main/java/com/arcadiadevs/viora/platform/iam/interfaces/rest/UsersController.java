package com.arcadiadevs.viora.platform.iam.interfaces.rest;

import com.arcadiadevs.viora.platform.iam.application.commandservices.UserCommandService;
import com.arcadiadevs.viora.platform.iam.application.queryservices.UserQueryService;
import com.arcadiadevs.viora.platform.iam.domain.model.queries.GetAllUsersQuery;
import com.arcadiadevs.viora.platform.iam.domain.model.queries.GetUserByIdQuery;
import com.arcadiadevs.viora.platform.iam.interfaces.rest.resources.ChangePasswordResource;
import com.arcadiadevs.viora.platform.iam.interfaces.rest.resources.UserResource;
import com.arcadiadevs.viora.platform.iam.interfaces.rest.transform.ChangePasswordCommandFromResourceAssembler;
import com.arcadiadevs.viora.platform.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import com.arcadiadevs.viora.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller that exposes IAM user resources.
 */
@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "User management endpoints")
public class UsersController {
    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;

    public UsersController(UserQueryService userQueryService, UserCommandService userCommandService) {
        this.userQueryService = userQueryService;
        this.userCommandService = userCommandService;
    }

    /**
     * Retrieves all users.
     *
     * @return list of user resources
     * @see UserResource
     */
    @GetMapping
    @Operation(
        summary = "Get all users",
        description = "Retrieves a list of all users in the system with their roles.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200",
                description = "Users retrieved successfully",
                content = @Content(schema = @Schema(implementation = UserResource.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
    })
    public ResponseEntity<List<UserResource>> getAllUsers() {
        var getAllUsersQuery = new GetAllUsersQuery();
        var users = userQueryService.handle(getAllUsersQuery);
        var userResources = users.stream().map(UserResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(userResources);
    }

    /**
     * Retrieves a user by identifier.
     *
     * @param userId user identifier
     * @return user resource when found
     * @see UserResource
     */
    @GetMapping(value = "/{userId}")
    @Operation(
        summary = "Get user by ID",
        description = "Retrieves a specific user's information by unique identifier.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200",
                description = "User retrieved successfully",
                content = @Content(schema = @Schema(implementation = UserResource.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResource> getUserById(
            @PathVariable
            @Parameter(
                description = "Unique user identifier",
                example = "1",
                required = true
            )
            Long userId
    ) {
        var getUserByIdQuery = new GetUserByIdQuery(userId);
        var user = userQueryService.handle(getUserByIdQuery);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(user.get());
        return ResponseEntity.ok(userResource);
    }

    /**
     * Changes a user's password.
     *
     * @param userId user identifier
     * @param resource change password request containing current and new passwords
     * @return ok response with empty body, or error details
     */
    @PutMapping(value = "/{userId}/password")
    @Operation(
        summary = "Change user password",
        description = "Changes the authenticated user's password. Requires current password verification.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid current password or new password policy violation", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> changePassword(
            @PathVariable
            @Parameter(description = "Unique user identifier", example = "1", required = true)
            Long userId,
            @RequestBody ChangePasswordResource resource
    ) {
        var command = ChangePasswordCommandFromResourceAssembler.toCommandFromResource(resource, userId);
        var result = userCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                UserResourceFromEntityAssembler::toResourceFromEntity,
                org.springframework.http.HttpStatus.OK
        );
    }
}
