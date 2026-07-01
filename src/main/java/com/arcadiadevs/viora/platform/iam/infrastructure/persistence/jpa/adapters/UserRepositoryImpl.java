package com.arcadiadevs.viora.platform.iam.infrastructure.persistence.jpa.adapters;

import com.arcadiadevs.viora.platform.iam.domain.model.aggregates.User;
import com.arcadiadevs.viora.platform.iam.domain.model.valueobjects.Username;
import com.arcadiadevs.viora.platform.iam.domain.repositories.UserRepository;
import com.arcadiadevs.viora.platform.iam.infrastructure.persistence.jpa.assemblers.UserPersistenceAssembler;
import com.arcadiadevs.viora.platform.iam.infrastructure.persistence.jpa.repositories.UserPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository adapter that bridges the IAM user domain repository port with Spring Data JPA.
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserPersistenceRepository userPersistenceRepository;

    public UserRepositoryImpl(UserPersistenceRepository userPersistenceRepository) {
        this.userPersistenceRepository = userPersistenceRepository;
    }

    @Override
    public Optional<User> findById(Long id) {
        return userPersistenceRepository.findById(id).map(UserPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<User> findByUsername(Username username) {
        return userPersistenceRepository.findByUsername(username.username()).map(UserPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<User> findAll() {
        return userPersistenceRepository.findAll().stream().map(UserPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public User save(User user) {
        var saved = userPersistenceRepository.save(UserPersistenceAssembler.toPersistenceFromDomain(user));
        return UserPersistenceAssembler.toDomainFromPersistence(saved);
    }

    @Override
    public boolean existsByUsername(Username username) {
        return userPersistenceRepository.existsByUsername(username.username());
    }
}

