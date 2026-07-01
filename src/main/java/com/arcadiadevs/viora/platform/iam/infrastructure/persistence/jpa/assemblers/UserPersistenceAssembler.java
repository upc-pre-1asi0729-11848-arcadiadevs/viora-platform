package com.arcadiadevs.viora.platform.iam.infrastructure.persistence.jpa.assemblers;

import com.arcadiadevs.viora.platform.iam.domain.model.aggregates.User;
import com.arcadiadevs.viora.platform.iam.domain.model.valueobjects.Username;
import com.arcadiadevs.viora.platform.iam.infrastructure.persistence.jpa.entities.UserPersistenceEntity;



/**
 * Static assembler between IAM user domain and persistence representations.
 */
public final class UserPersistenceAssembler {

    private UserPersistenceAssembler() {
    }

    public static User toDomainFromPersistence(UserPersistenceEntity entity) {
        if (entity == null) return null;
        var domain = new User();
        domain.setId(entity.getId());
        domain.setUsername(new Username(entity.getUsername()));
        domain.setPassword(entity.getPassword());
        domain.setRole(entity.getRole() != null ? RolePersistenceAssembler.toDomainFromPersistence(entity.getRole()) : null);
        return domain;
    }

    public static UserPersistenceEntity toPersistenceFromDomain(User user) {
        if (user == null) return null;
        var entity = new UserPersistenceEntity();
        // Only set ID if the user is being updated (has a non-null ID)
        // For new users, leave ID null to allow JPA to generate it
        if (user.getId() != null) {
            entity.setId(user.getId());
        }
        entity.setUsername(user.getUsername().username());
        entity.setPassword(user.getPassword());
        entity.setRole(user.getRole() != null ? RolePersistenceAssembler.toPersistenceFromDomain(user.getRole()) : null);
        return entity;
    }
}

