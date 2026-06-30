package com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.repositories;

import com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.entities.ServiceProposalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceProposalJpaRepository extends JpaRepository<ServiceProposalEntity, Long> {
    
    List<ServiceProposalEntity> findByInterventionRequestId(Long interventionRequestId);
    
    List<ServiceProposalEntity> findBySpecialistId(Long specialistId);
}
