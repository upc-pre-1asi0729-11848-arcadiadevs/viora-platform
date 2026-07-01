package com.arcadiadevs.viora.platform.agronomic.application.queryservices;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Expense;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetGrowerExpensesQuery;

import java.util.List;

/**
 * Service to handle queries related to expenses.
 */
public interface ExpenseQueryService {

    List<Expense> handle(GetGrowerExpensesQuery query);
}
