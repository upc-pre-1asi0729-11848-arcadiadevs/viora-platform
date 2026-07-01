package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.CreateExpenseCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ExpenseCategory;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ExpenseStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ExpenseType;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PaymentStatus;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.CreateExpenseResource;

public class CreateExpenseCommandFromResourceAssembler {

    public static CreateExpenseCommand toCommandFromResource(CreateExpenseResource resource) {
        return new CreateExpenseCommand(
                resource.growerId(),
                resource.plotId(),
                parseType(resource.type()),
                parseCategory(resource.category()),
                resource.linkedActionCode(),
                resource.amount(),
                resource.currency(),
                resource.expenseDate(),
                parsePaymentStatus(resource.paymentStatus()),
                resource.note(),
                parseStatus(resource.status())
        );
    }

    private static ExpenseType parseType(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Expense type is required.");
        }
        return ExpenseType.valueOf(value.trim().toUpperCase());
    }

    private static ExpenseCategory parseCategory(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Expense category is required.");
        }
        return ExpenseCategory.valueOf(value.trim().toUpperCase());
    }

    private static PaymentStatus parsePaymentStatus(String value) {
        return value == null || value.isBlank()
                ? null : PaymentStatus.valueOf(value.trim().toUpperCase());
    }

    private static ExpenseStatus parseStatus(String value) {
        return value == null || value.isBlank()
                ? null : ExpenseStatus.valueOf(value.trim().toUpperCase());
    }
}
