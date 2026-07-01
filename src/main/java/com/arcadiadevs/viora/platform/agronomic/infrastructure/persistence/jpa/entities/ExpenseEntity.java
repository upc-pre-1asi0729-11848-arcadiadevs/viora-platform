package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.entities;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ExpenseCategory;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ExpenseStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ExpenseType;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PaymentStatus;
import com.arcadiadevs.viora.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * JPA entity mapping the Expense aggregate to the {@code expenses} table.
 */
@Entity
@Table(name = "expenses")
@Getter
@Setter
public class ExpenseEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "grower_id", nullable = false)
    private Long growerId;

    @Column(name = "plot_id", nullable = false)
    private Long plotId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ExpenseType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ExpenseCategory category;

    @Column(name = "linked_action_code")
    private String linkedActionCode;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency;

    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ExpenseStatus status;
}
