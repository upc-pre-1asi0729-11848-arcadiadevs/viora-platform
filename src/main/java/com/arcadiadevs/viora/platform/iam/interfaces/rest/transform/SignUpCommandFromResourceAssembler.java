package com.arcadiadevs.viora.platform.iam.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.iam.domain.model.commands.SignUpCommand;
import com.arcadiadevs.viora.platform.iam.domain.model.entities.Role;
import com.arcadiadevs.viora.platform.iam.domain.model.valueobjects.Password;
import com.arcadiadevs.viora.platform.iam.domain.model.valueobjects.Username;
import com.arcadiadevs.viora.platform.iam.interfaces.rest.resources.SignUpResource;



/**
 * Assembler that translates {@link SignUpResource} into {@link SignUpCommand}.
 */
public class SignUpCommandFromResourceAssembler {
    /**
     * Converts the incoming sign-up resource to an application command.
     *
     * @param resource sign-up payload from REST API
     * @return sign-up command consumed by the application layer
     */
    public static SignUpCommand toCommandFromResource(SignUpResource resource) {
        var role = resource.role() != null ? Role.toRoleFromName(resource.role()) : Role.getDefaultRole();
        return new SignUpCommand(new Username(resource.username()), new Password(resource.password()), role);
    }
}
