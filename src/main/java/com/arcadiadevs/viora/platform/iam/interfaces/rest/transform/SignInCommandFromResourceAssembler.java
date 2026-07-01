package com.arcadiadevs.viora.platform.iam.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.iam.domain.model.commands.SignInCommand;
import com.arcadiadevs.viora.platform.iam.domain.model.valueobjects.Password;
import com.arcadiadevs.viora.platform.iam.domain.model.valueobjects.Username;
import com.arcadiadevs.viora.platform.iam.interfaces.rest.resources.SignInResource;

/**
 * Assembler that translates {@link SignInResource} into {@link SignInCommand}.
 */
public class SignInCommandFromResourceAssembler {
    /**
     * Converts the incoming sign-in resource to an application command.
     *
     * @param signInResource sign-in payload from REST API
     * @return sign-in command consumed by the application layer
     */
    public static SignInCommand toCommandFromResource(SignInResource signInResource) {
        return new SignInCommand(new Username(signInResource.username()), new Password(signInResource.password()));
    }
}
