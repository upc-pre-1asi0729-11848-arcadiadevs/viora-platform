package com.arcadiadevs.viora.platform.iam.domain.repositories;

import com.arcadiadevs.viora.platform.iam.domain.model.aggregates.User;
import com.arcadiadevs.viora.platform.iam.domain.model.valueobjects.Username;

import java.util.List;
import java.util.Optional;

/**
 * IAM user repository port.
 */
public interface UserRepository {
    Optional<User> findById(Long id);

    Optional<User> findByUsername(Username username);

    List<User> findAll();

    User save(User user);

    boolean existsByUsername(Username username);
}

