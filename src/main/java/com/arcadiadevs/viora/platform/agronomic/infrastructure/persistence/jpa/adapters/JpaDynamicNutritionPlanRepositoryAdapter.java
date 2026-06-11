package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.adapters;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.DynamicNutritionPlan;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DynamicNutritionPlanId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NutritionPlanStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.DynamicNutritionPlanRepository;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.assemblers.DynamicNutritionPlanEntityFromDynamicNutritionPlanAssembler;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.assemblers.DynamicNutritionPlanFromDynamicNutritionPlanEntityAssembler;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.repositories.SpringDataDynamicNutritionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA implementation of the DynamicNutritionPlanRepository domain port.
 */
@Repository
@RequiredArgsConstructor
public class JpaDynamicNutritionPlanRepositoryAdapter implements DynamicNutritionPlanRepository {

    private final SpringDataDynamicNutritionPlanRepository springDataDynamicNutritionPlanRepository;

    @Override
    public Optional<DynamicNutritionPlan> findById(DynamicNutritionPlanId id) {
        return springDataDynamicNutritionPlanRepository.findById(id.getValue())
                .map(DynamicNutritionPlanFromDynamicNutritionPlanEntityAssembler::toAggregateFromEntity);
    }

    @Override
    public Optional<DynamicNutritionPlan> findActiveByUserIdAndPlotId(UserId userId, PlotId plotId) {
        return springDataDynamicNutritionPlanRepository
                .findFirstByUserIdAndPlotIdAndStatusOrderByGeneratedDateDesc(
                        userId.getValue(),
                        plotId.getValue(),
                        NutritionPlanStatus.ACTIVE
                )
                .map(DynamicNutritionPlanFromDynamicNutritionPlanEntityAssembler::toAggregateFromEntity);
    }

    @Override
    public DynamicNutritionPlan save(DynamicNutritionPlan dynamicNutritionPlan) {
        var entity = DynamicNutritionPlanEntityFromDynamicNutritionPlanAssembler
                .toEntityFromAggregate(dynamicNutritionPlan);

        var savedEntity = springDataDynamicNutritionPlanRepository.save(entity);

        return DynamicNutritionPlanFromDynamicNutritionPlanEntityAssembler
                .toAggregateFromEntity(savedEntity);
    }
}
