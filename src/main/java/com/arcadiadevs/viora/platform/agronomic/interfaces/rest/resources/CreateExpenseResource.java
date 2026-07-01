package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request body for registering an expense (`POST /api/v1/expenses`). Enum fields
 * are sent as their string name (e.g. type "CLIMATE_MITIGATION").
 */
public record CreateExpenseResource(
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
        String status
) {
}
