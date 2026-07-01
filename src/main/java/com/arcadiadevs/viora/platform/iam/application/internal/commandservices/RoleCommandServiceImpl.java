package com.arcadiadevs.viora.platform.iam.application.internal.commandservices;

import com.arcadiadevs.viora.platform.iam.application.commandservices.RoleCommandService;
import com.arcadiadevs.viora.platform.iam.domain.model.commands.SeedRolesCommand;
import com.arcadiadevs.viora.platform.iam.domain.model.entities.Role;
import com.arcadiadevs.viora.platform.iam.domain.model.valueobjects.Roles;
import com.arcadiadevs.viora.platform.iam.domain.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * Implementation of {@link RoleCommandService} to handle {@link SeedRolesCommand}.
 */
@Service
public class RoleCommandServiceImpl implements RoleCommandService {

    private final RoleRepository roleRepository;

    public RoleCommandServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void handle(SeedRolesCommand command) {
        Arrays.stream(Roles.values()).forEach(role -> {
            if (!roleRepository.existsByName(role)) {
                roleRepository.save(new Role(Roles.valueOf(role.name())));
            }
        });
    }
}
