package com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.repositories;

import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.TreatmentPrescriptionId;
import com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.entities.InterventionExecutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InterventionExecutionJpaRepository extends JpaRepository<InterventionExecutionEntity, Long> {
    Optional<InterventionExecutionEntity> findByTreatmentPrescriptionId(TreatmentPrescriptionId treatmentPrescriptionId);
    boolean existsByTreatmentPrescriptionId(TreatmentPrescriptionId treatmentPrescriptionId);
}
