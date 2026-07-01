package com.arcadiadevs.viora.platform.iam.interfaces.acl;

import com.arcadiadevs.viora.platform.iam.application.commandservices.UserCommandService;
import com.arcadiadevs.viora.platform.iam.application.queryservices.UserQueryService;
import com.arcadiadevs.viora.platform.iam.domain.model.commands.SignUpCommand;
import com.arcadiadevs.viora.platform.iam.domain.model.entities.Role;
import com.arcadiadevs.viora.platform.iam.domain.model.queries.GetUserByIdQuery;
import com.arcadiadevs.viora.platform.iam.domain.model.queries.GetUserByUsernameQuery;
import com.arcadiadevs.viora.platform.iam.domain.model.valueobjects.Password;
import com.arcadiadevs.viora.platform.iam.domain.model.valueobjects.Username;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * ACL facade that exposes IAM bounded context capabilities to other contexts.
 *
 * <p>Provides a simplified integration surface for creating users and querying identity data
 * without leaking IAM internal model details.</p>
 */
public class IamContextFacade {
    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    public IamContextFacade(UserCommandService userCommandService, UserQueryService userQueryService) {
        this.userCommandService = userCommandService;
        this.userQueryService = userQueryService;
    }

    /**
     * Creates a new user assigning the default role.
     *
     * @param username username to register
     * @param password raw password
     * @return created user identifier, or {@code 0L} when creation fails
     */
    public Long createUser(String username, String password) {
        var signUpCommand = new SignUpCommand(new Username(username), new Password(password), Role.getDefaultRole());
        var result = userCommandService.handle(signUpCommand);
        if (result instanceof com.arcadiadevs.viora.platform.shared.application.result.Result.Success(var user)) {
            return user.getId();
        }
        return 0L;
    }

    /**
     * Creates a new user with explicit role names.
     *
     * @param username username to register
     * @param password raw password
     * @param roleNames role names to assign; unknown names are ignored
     * @return created user identifier, or {@code 0L} when creation fails
     */
    public Long createUser(String username, String password, String roleName) {
        var role = roleName != null ? Role.toRoleFromName(roleName) : Role.getDefaultRole();
        var signUpCommand = new SignUpCommand(new Username(username), new Password(password), role);
        var result = userCommandService.handle(signUpCommand);
        if (result instanceof com.arcadiadevs.viora.platform.shared.application.result.Result.Success(var user)) {
            return user.getId();
        }
        return 0L;
    }

    /**
     * Fetches the identifier for a username.
     *
     * @param username username to search
     * @return user identifier, or {@code 0L} when user is not found
     */
    public Long fetchUserIdByUsername(String username) {
        var getUserByUsernameQuery = new GetUserByUsernameQuery(new Username(username));
        var result = userQueryService.handle(getUserByUsernameQuery);
        if (result.isEmpty()) return 0L;
        return result.get().getId();
    }

    /**
     * Fetches the username for a user identifier.
     *
     * @param userId user identifier
     * @return username, or an empty string when user is not found
     */
    public String fetchUsernameByUserId(Long userId) {
        var getUserByIdQuery = new GetUserByIdQuery(userId);
        var result = userQueryService.handle(getUserByIdQuery);
        if (result.isEmpty()) return Strings.EMPTY;
        return result.get().getUsername().username();
    }

}
