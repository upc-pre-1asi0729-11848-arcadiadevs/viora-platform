package com.arcadiadevs.viora.platform.iam.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.iam.domain.model.commands.ChangePasswordCommand;
import com.arcadiadevs.viora.platform.iam.domain.model.valueobjects.Password;
import com.arcadiadevs.viora.platform.iam.interfaces.rest.resources.ChangePasswordResource;

/**
 * Assembler that translates {@link ChangePasswordResource} into {@link ChangePasswordCommand}.
 */
public class ChangePasswordCommandFromResourceAssembler {

    /**
     * Converts the incoming change password resource to an application command.
     *
     * @param resource change password payload from REST API
     * @param userId the ID of the user
     * @return change password command consumed by the application layer
     */
    public static ChangePasswordCommand toCommandFromResource(ChangePasswordResource resource, Long userId) {
        return new ChangePasswordCommand(userId, resource.currentPassword(), new Password(resource.newPassword()));
    }
}
