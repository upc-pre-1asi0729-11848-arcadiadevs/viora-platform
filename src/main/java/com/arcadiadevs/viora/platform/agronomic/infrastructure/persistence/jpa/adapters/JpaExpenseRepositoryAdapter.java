package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.adapters;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Expense;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ExpenseId;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.ExpenseRepository;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.assemblers.ExpenseEntityAssembler;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.repositories.ExpenseJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA adapter implementing the {@link ExpenseRepository} domain port.
 */
@Repository
public class JpaExpenseRepositoryAdapter implements ExpenseRepository {

    private final ExpenseJpaRepository jpaRepository;

    public JpaExpenseRepositoryAdapter(ExpenseJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Expense save(Expense expense) {
        var entity = ExpenseEntityAssembler.toEntity(expense);
        var saved = jpaRepository.save(entity);
        return ExpenseEntityAssembler.toDomain(saved);
    }

    @Override
    public Optional<Expense> findById(ExpenseId id) {
        return jpaRepository.findById(id.value()).map(ExpenseEntityAssembler::toDomain);
    }

    @Override
    public List<Expense> findByGrowerId(Long growerId) {
        return jpaRepository.findByGrowerIdOrderByExpenseDateDesc(growerId).stream()
                .map(ExpenseEntityAssembler::toDomain)
                .toList();
    }

    @Override
    public List<Expense> findByGrowerIdAndPlotId(Long growerId, Long plotId) {
        return jpaRepository.findByGrowerIdAndPlotIdOrderByExpenseDateDesc(growerId, plotId).stream()
                .map(ExpenseEntityAssembler::toDomain)
                .toList();
    }
}
