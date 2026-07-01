package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.assemblers;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Expense;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.CreateExpenseCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ExpenseId;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.entities.ExpenseEntity;

public class ExpenseEntityAssembler {

    public static ExpenseEntity toEntity(Expense domain) {
        var entity = new ExpenseEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId().value());
        }
        entity.setGrowerId(domain.getGrowerId());
        entity.setPlotId(domain.getPlotId());
        entity.setType(domain.getType());
        entity.setCategory(domain.getCategory());
        entity.setLinkedActionCode(domain.getLinkedActionCode());
        entity.setAmount(domain.getAmount());
        entity.setCurrency(domain.getCurrency());
        entity.setExpenseDate(domain.getExpenseDate());
        entity.setPaymentStatus(domain.getPaymentStatus());
        entity.setNote(domain.getNote());
        entity.setStatus(domain.getStatus());
        return entity;
    }

    public static Expense toDomain(ExpenseEntity entity) {
        var command = new CreateExpenseCommand(
                entity.getGrowerId(),
                entity.getPlotId(),
                entity.getType(),
                entity.getCategory(),
                entity.getLinkedActionCode(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getExpenseDate(),
                entity.getPaymentStatus(),
                entity.getNote(),
                entity.getStatus()
        );
        var domain = new Expense(command);
        domain.restoreIdentity(new ExpenseId(entity.getId()));
        domain.restoreAudit(entity.getCreatedAt(), entity.getUpdatedAt());
        return domain;
    }
}
