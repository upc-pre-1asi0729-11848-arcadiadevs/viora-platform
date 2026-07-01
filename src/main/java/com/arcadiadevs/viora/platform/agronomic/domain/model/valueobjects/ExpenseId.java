package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

/**
 * Value object representing a unique identifier for an Expense.
 */
public record ExpenseId(Long value) {
    public ExpenseId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Expense ID must be a positive number.");
        }
    }
}
