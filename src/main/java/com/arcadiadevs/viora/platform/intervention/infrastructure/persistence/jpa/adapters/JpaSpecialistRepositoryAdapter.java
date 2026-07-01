package com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.adapters;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.Specialist;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.SpecialistId;
import com.arcadiadevs.viora.platform.intervention.domain.repositories.SpecialistRepository;
import com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.assemblers.SpecialistEntityAssembler;
import com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.repositories.SpecialistJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaSpecialistRepositoryAdapter implements SpecialistRepository {

    private final SpecialistJpaRepository jpaRepository;

    public JpaSpecialistRepositoryAdapter(SpecialistJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Specialist save(Specialist specialist) {
        var entity = SpecialistEntityAssembler.toEntity(specialist);
        var saved = jpaRepository.save(entity);
        return SpecialistEntityAssembler.toDomain(saved);
    }

    @Override
    public Optional<Specialist> findById(SpecialistId id) {
        return jpaRepository.findById(id.value()).map(SpecialistEntityAssembler::toDomain);
    }

    @Override
    public List<Specialist> findAll() {
        return jpaRepository.findAll().stream().map(SpecialistEntityAssembler::toDomain).toList();
    }

    @Override
    public boolean existsByFullName(String fullName) {
        return jpaRepository.existsByFullName(fullName);
    }
}
