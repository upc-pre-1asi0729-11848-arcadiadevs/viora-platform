package com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.repositories;

import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.InterventionStatus;
import com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.entities.InterventionRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for InterventionRequest entities.
 */
@Repository
public interface InterventionRequestJpaRepository extends JpaRepository<InterventionRequestEntity, Long> {
    
    List<InterventionRequestEntity> findByGrowerId(Long growerId);

    List<InterventionRequestEntity> findByGrowerIdAndPlotId(Long growerId, Long plotId);

    List<InterventionRequestEntity> findBySpecialistIdAndStatus(Long specialistId, InterventionStatus status);
}
