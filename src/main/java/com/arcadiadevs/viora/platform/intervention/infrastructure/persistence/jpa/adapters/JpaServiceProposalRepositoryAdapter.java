package com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.adapters;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.ServiceProposal;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ServiceProposalId;
import com.arcadiadevs.viora.platform.intervention.domain.repositories.ServiceProposalRepository;
import com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.assemblers.ServiceProposalEntityAssembler;
import com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.repositories.ServiceProposalJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA adapter for the {@link ServiceProposalRepository} interface.
 * Implements the repository operations using Spring Data JPA.
 */
@Repository
public class JpaServiceProposalRepositoryAdapter implements ServiceProposalRepository {

    private final ServiceProposalJpaRepository jpaRepository;

    /**
     * Constructs a new JpaServiceProposalRepositoryAdapter.
     *
     * @param jpaRepository the underlying Spring Data JPA repository
     */
    public JpaServiceProposalRepositoryAdapter(ServiceProposalJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ServiceProposal save(ServiceProposal serviceProposal) {
        var entity = ServiceProposalEntityAssembler.toEntity(serviceProposal);
        var savedEntity = jpaRepository.save(entity);
        return ServiceProposalEntityAssembler.toDomain(savedEntity);
    }

    @Override
    public Optional<ServiceProposal> findById(ServiceProposalId id) {
        return jpaRepository.findById(id.value())
                .map(ServiceProposalEntityAssembler::toDomain);
    }

    @Override
    public List<ServiceProposal> findByInterventionRequestId(Long interventionRequestId) {
        return jpaRepository.findByInterventionRequestId(interventionRequestId).stream()
                .map(ServiceProposalEntityAssembler::toDomain)
                .toList();
    }

    @Override
    public List<ServiceProposal> findBySpecialistId(Long specialistId) {
        return jpaRepository.findBySpecialistId(specialistId).stream()
                .map(ServiceProposalEntityAssembler::toDomain)
                .toList();
    }
}
