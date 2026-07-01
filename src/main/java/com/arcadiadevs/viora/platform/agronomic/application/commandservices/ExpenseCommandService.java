package com.arcadiadevs.viora.platform.agronomic.application.commandservices;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Expense;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.CreateExpenseCommand;

import java.util.Optional;

/**
 * Service to handle commands related to expenses.
 */
public interface ExpenseCommandService {

    Optional<Expense> handle(CreateExpenseCommand command);
}
