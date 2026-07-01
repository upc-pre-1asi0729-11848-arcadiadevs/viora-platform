package com.arcadiadevs.viora.platform.intervention.domain.repositories;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.Specialist;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.SpecialistId;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository (port) for the Specialist aggregate.
 */
public interface SpecialistRepository {

    Specialist save(Specialist specialist);

    Optional<Specialist> findById(SpecialistId id);

    List<Specialist> findAll();

    boolean existsByFullName(String fullName);
}
