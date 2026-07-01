package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.repositories;

import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.entities.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseJpaRepository extends JpaRepository<ExpenseEntity, Long> {

    List<ExpenseEntity> findByGrowerIdOrderByExpenseDateDesc(Long growerId);

    List<ExpenseEntity> findByGrowerIdAndPlotIdOrderByExpenseDateDesc(Long growerId, Long plotId);
}
