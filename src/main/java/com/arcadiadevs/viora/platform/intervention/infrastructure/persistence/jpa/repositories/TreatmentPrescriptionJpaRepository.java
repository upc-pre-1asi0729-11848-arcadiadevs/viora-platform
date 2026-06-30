package com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.repositories;

import com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.entities.TreatmentPrescriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA repository for TreatmentPrescriptionEntity.
 */
public interface TreatmentPrescriptionJpaRepository extends JpaRepository<TreatmentPrescriptionEntity, Long> {
    Optional<TreatmentPrescriptionEntity> findByServiceProposalId(Long serviceProposalId);
}
