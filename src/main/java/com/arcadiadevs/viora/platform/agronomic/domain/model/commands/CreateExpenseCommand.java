package com.arcadiadevs.viora.platform.agronomic.domain.model.commands;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ExpenseCategory;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ExpenseStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ExpenseType;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Command to register a new operational expense for a plot.
 */
public record CreateExpenseCommand(
        Long growerId,
        Long plotId,
        ExpenseType type,
        ExpenseCategory category,
        String linkedActionCode,
        BigDecimal amount,
        String currency,
        LocalDate expenseDate,
        PaymentStatus paymentStatus,
        String note,
        ExpenseStatus status
) {
    public CreateExpenseCommand {
        if (growerId == null || growerId <= 0) {
            throw new IllegalArgumentException("Grower ID is required and must be positive.");
        }
        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException("Plot ID is required and must be positive.");
        }
        if (type == null) {
            throw new IllegalArgumentException("Expense type is required.");
        }
        if (category == null) {
            throw new IllegalArgumentException("Expense category is required.");
        }
        if (amount == null || amount.signum() < 0) {
            throw new IllegalArgumentException("Amount is required and must not be negative.");
        }
        if (expenseDate == null) {
            throw new IllegalArgumentException("Expense date is required.");
        }
    }
}
