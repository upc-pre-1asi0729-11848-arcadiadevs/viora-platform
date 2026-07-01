package com.arcadiadevs.viora.platform.agronomic.application.internal.commandservices;

import com.arcadiadevs.viora.platform.agronomic.application.commandservices.ExpenseCommandService;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Expense;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.CreateExpenseCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ExpenseCommandServiceImpl implements ExpenseCommandService {

    private final ExpenseRepository expenseRepository;

    public ExpenseCommandServiceImpl(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Override
    @Transactional
    public Optional<Expense> handle(CreateExpenseCommand command) {
        var expense = new Expense(command);
        return Optional.of(expenseRepository.save(expense));
    }
}
