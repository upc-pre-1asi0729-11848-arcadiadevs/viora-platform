package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Expense;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.ExpenseResource;

public class ExpenseResourceFromEntityAssembler {

    public static ExpenseResource toResourceFromEntity(Expense expense) {
        return new ExpenseResource(
                expense.getId() != null ? expense.getId().value() : null,
                expense.getGrowerId(),
                expense.getPlotId(),
                expense.getType() != null ? expense.getType().name() : null,
                expense.getCategory() != null ? expense.getCategory().name() : null,
                expense.getLinkedActionCode(),
                expense.getAmount(),
                expense.getCurrency(),
                expense.getExpenseDate(),
                expense.getPaymentStatus() != null ? expense.getPaymentStatus().name() : null,
                expense.getNote(),
                expense.getStatus() != null ? expense.getStatus().name() : null,
                expense.getCreatedAt(),
                expense.getUpdatedAt()
        );
    }
}
