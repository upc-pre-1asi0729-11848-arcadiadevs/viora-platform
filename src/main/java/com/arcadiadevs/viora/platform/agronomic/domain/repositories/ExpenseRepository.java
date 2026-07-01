package com.arcadiadevs.viora.platform.agronomic.domain.repositories;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Expense;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ExpenseId;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository for the Expense aggregate.
 */
public interface ExpenseRepository {

    Expense save(Expense expense);

    Optional<Expense> findById(ExpenseId id);

    List<Expense> findByGrowerId(Long growerId);

    List<Expense> findByGrowerIdAndPlotId(Long growerId, Long plotId);
}
