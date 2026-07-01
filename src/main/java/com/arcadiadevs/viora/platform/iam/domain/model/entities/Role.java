package com.arcadiadevs.viora.platform.iam.domain.model.entities;

import com.arcadiadevs.viora.platform.iam.domain.model.valueobjects.Roles;
import lombok.*;

import java.util.List;

/**
 * Role domain entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
@EqualsAndHashCode
@ToString
public class Role {
    private Long id;
    private Roles name;

    public Role(Roles name) {
        this.name = name;
    }

    /**
     * Get the name of the role as a string.
     *
     * @return the name of the role as a string
     */
    public String getStringName() {
        return name.name();
    }

    /**
     * Get the default role.
     *
     * @return the default role
     */
    public static Role getDefaultRole() {
        return new Role(Roles.ROLE_GROWER);
    }

    /**
     * Get the role from its name.
     *
     * @param name the name of the role
     * @return the role
     */
    public static Role toRoleFromName(String name) {
        return new Role(Roles.valueOf(name));
    }
}
