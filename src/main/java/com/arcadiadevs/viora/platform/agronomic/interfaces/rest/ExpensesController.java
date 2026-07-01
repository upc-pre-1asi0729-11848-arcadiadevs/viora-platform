package com.arcadiadevs.viora.platform.agronomic.interfaces.rest;

import com.arcadiadevs.viora.platform.agronomic.application.commandservices.ExpenseCommandService;
import com.arcadiadevs.viora.platform.agronomic.application.queryservices.ExpenseQueryService;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetGrowerExpensesQuery;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.CreateExpenseResource;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.ExpenseResource;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform.CreateExpenseCommandFromResourceAssembler;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform.ExpenseResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for operational expenses (Agronomic Monitoring &amp; Prediction).
 */
@RestController
@RequestMapping(value = "/api/v1/expenses")
@Tag(name = "Expenses", description = "Endpoints for registering and listing operational expenses")
public class ExpensesController {

    private final ExpenseCommandService commandService;
    private final ExpenseQueryService queryService;

    public ExpensesController(ExpenseCommandService commandService, ExpenseQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @GetMapping
    @Operation(summary = "List a grower's expenses (optionally scoped to a plot)")
    public ResponseEntity<List<ExpenseResource>> getGrowerExpenses(
            @RequestParam Long growerId,
            @RequestParam(required = false) Long plotId) {
        var query = new GetGrowerExpensesQuery(growerId, plotId);
        var expenses = queryService.handle(query).stream()
                .map(ExpenseResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(expenses);
    }

    @PostMapping
    @Operation(summary = "Register a new operational expense")
    public ResponseEntity<ExpenseResource> createExpense(@RequestBody CreateExpenseResource resource) {
        var command = CreateExpenseCommandFromResourceAssembler.toCommandFromResource(resource);
        var expense = commandService.handle(command);
        if (expense.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ExpenseResourceFromEntityAssembler.toResourceFromEntity(expense.get()));
    }
}
