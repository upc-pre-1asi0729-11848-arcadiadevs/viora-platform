package com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.repositories;

import com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.entities.SpecialistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for Specialist entities.
 */
@Repository
public interface SpecialistJpaRepository extends JpaRepository<SpecialistEntity, Long> {

    boolean existsByFullName(String fullName);
}
