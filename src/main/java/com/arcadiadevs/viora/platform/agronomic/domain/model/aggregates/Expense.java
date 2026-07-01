package com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates;

import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.CreateExpenseCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ExpenseCategory;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ExpenseId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ExpenseStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ExpenseType;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PaymentStatus;
import com.arcadiadevs.viora.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Aggregate root for an operational Expense in the Subscription, Billing &amp;
 * Referral bounded context. Records a cost the producer incurred on one of their
 * plots (nutrition/climate mitigation or a phytosanitary intervention). Domain
 * logic only; no persistence annotations.
 */
@Getter
public class Expense extends AbstractDomainAggregateRoot<Expense> {

    private static final String DEFAULT_CURRENCY = "PEN";

    private ExpenseId id;

    // External bounded-context identifiers, referenced by simple id.
    private Long growerId;
    private Long plotId;

    private ExpenseType type;
    private ExpenseCategory category;
    /** Code of the plot action this cost is linked to (e.g. "DN-042", "INT-011"). */
    private String linkedActionCode;
    private BigDecimal amount;
    private String currency;
    private LocalDate expenseDate;
    private PaymentStatus paymentStatus;
    private String note;
    private ExpenseStatus status;

    private Instant createdAt;
    private Instant updatedAt;

    public Expense() {
        // Default constructor for reconstruction.
    }

    public Expense(CreateExpenseCommand command) {
        this.growerId = command.growerId();
        this.plotId = command.plotId();
        this.type = command.type();
        this.category = command.category();
        this.linkedActionCode = command.linkedActionCode();
        this.amount = command.amount();
        this.currency = command.currency() != null && !command.currency().isBlank()
                ? command.currency() : DEFAULT_CURRENCY;
        this.expenseDate = command.expenseDate();
        this.paymentStatus = command.paymentStatus() != null ? command.paymentStatus() : PaymentStatus.PAID;
        this.note = command.note();
        this.status = command.status() != null ? command.status() : ExpenseStatus.REGISTERED;
    }

    public Expense restoreIdentity(ExpenseId id) {
        if (id == null) {
            throw new IllegalArgumentException("Expense ID is required.");
        }
        this.id = id;
        return this;
    }

    /** Restores the persisted audit timestamps when rebuilding from storage. */
    public Expense restoreAudit(Instant createdAt, Instant updatedAt) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        return this;
    }
}
