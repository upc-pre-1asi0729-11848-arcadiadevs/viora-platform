package com.arcadiadevs.viora.platform.agronomic.application.internal.queryservices;

import com.arcadiadevs.viora.platform.agronomic.application.queryservices.ExpenseQueryService;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Expense;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetGrowerExpensesQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseQueryServiceImpl implements ExpenseQueryService {

    private final ExpenseRepository expenseRepository;

    public ExpenseQueryServiceImpl(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Override
    public List<Expense> handle(GetGrowerExpensesQuery query) {
        if (query.plotId() != null) {
            return expenseRepository.findByGrowerIdAndPlotId(query.growerId(), query.plotId());
        }
        return expenseRepository.findByGrowerId(query.growerId());
    }
}
