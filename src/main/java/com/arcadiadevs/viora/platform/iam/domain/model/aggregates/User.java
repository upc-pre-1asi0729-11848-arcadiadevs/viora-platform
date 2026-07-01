package com.arcadiadevs.viora.platform.iam.domain.model.aggregates;

import com.arcadiadevs.viora.platform.iam.domain.model.entities.Role;
import com.arcadiadevs.viora.platform.iam.domain.model.valueobjects.Username;
import com.arcadiadevs.viora.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;



/**
 * User aggregate root.
 */
@Getter
public class User extends AbstractDomainAggregateRoot<User> {

    @Setter
    private Long id;
    @Setter
    private Username username;
    @Setter
    private String password;
    @Setter
    private Role role;

    public User() {
    }

    public User(Username username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(Username username, String password, Role role) {
        this(username, password);
        this.role = role;
    }

    public void changePassword(String newPasswordHash) {
        this.password = newPasswordHash;
    }
}
