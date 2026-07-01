package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Response shape for an expense record.
 */
public record ExpenseResource(
        Long id,
        Long growerId,
        Long plotId,
        String type,
        String category,
        String linkedActionCode,
        BigDecimal amount,
        String currency,
        LocalDate expenseDate,
        String paymentStatus,
        String note,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
}
