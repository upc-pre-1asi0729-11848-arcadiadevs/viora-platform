package com.arcadiadevs.viora.platform.agronomic.application.queryservices;

import com.arcadiadevs.viora.platform.agronomic.application.internal.PlotOwnershipValidator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.DynamicNutritionPlan;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetActiveDynamicNutritionPlanQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AreaSize;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ClimateRiskLevel;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DynamicNutritionPlanId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviValue;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NutritionApplicationWindow;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NutritionInputRecommendation;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NutritionInputStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlanRationale;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotName;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PolygonCoordinates;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.DynamicNutritionPlanRepository;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.PlotRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DynamicNutritionPlanQueryServiceTest {

    @Test
    void returnsActivePlanForOwnedPlot() {
        var plotRepository = new InMemoryPlotRepository();
        plotRepository.plot = createPlot(10L, 1L);
        var planRepository = new InMemoryDynamicNutritionPlanRepository();
        planRepository.plan = createActivePlan(10L, 1L);
        var service = new DynamicNutritionPlanQueryService(
                new PlotOwnershipValidator(plotRepository),
                planRepository
        );

        var result = service.handle(new GetActiveDynamicNutritionPlanQuery(10L, 1L));

        assertTrue(result.isSuccess());
        assertEquals(1L, result.success().orElseThrow().getPlotId().getValue());
    }

    @Test
    void returnsNotFoundWhenNoActivePlanExists() {
        var plotRepository = new InMemoryPlotRepository();
        plotRepository.plot = createPlot(10L, 1L);
        var planRepository = new InMemoryDynamicNutritionPlanRepository();
        var service = new DynamicNutritionPlanQueryService(
                new PlotOwnershipValidator(plotRepository),
                planRepository
        );

        var result = service.handle(new GetActiveDynamicNutritionPlanQuery(10L, 1L));

        assertTrue(result.isFailure());
        assertEquals("DYNAMIC_NUTRITION_PLAN_NOT_FOUND", result.failure().orElseThrow().code());
    }

    @Test
    void returnsNotFoundWhenPlotDoesNotExist() {
        var plotRepository = new InMemoryPlotRepository();
        var planRepository = new InMemoryDynamicNutritionPlanRepository();
        var service = new DynamicNutritionPlanQueryService(
                new PlotOwnershipValidator(plotRepository),
                planRepository
        );

        var result = service.handle(new GetActiveDynamicNutritionPlanQuery(10L, 1L));

        assertTrue(result.isFailure());
        assertEquals("PLOT_NOT_FOUND", result.failure().orElseThrow().code());
    }

    @Test
    void returnsForbiddenWhenPlotIsNotOwnedByUser() {
        var plotRepository = new InMemoryPlotRepository();
        plotRepository.plot = createPlot(99L, 1L);
        var planRepository = new InMemoryDynamicNutritionPlanRepository();
        var service = new DynamicNutritionPlanQueryService(
                new PlotOwnershipValidator(plotRepository),
                planRepository
        );

        var result = service.handle(new GetActiveDynamicNutritionPlanQuery(10L, 1L));

        assertTrue(result.isFailure());
        assertEquals("PLOT_OWNERSHIP_FORBIDDEN", result.failure().orElseThrow().code());
    }

    private static DynamicNutritionPlan createActivePlan(Long userId, Long plotId) {
        var today = LocalDate.of(2026, 6, 11);
        var plan = DynamicNutritionPlan.recommend(
                new UserId(userId),
                new PlotId(plotId),
                List.of(new NutritionInputRecommendation(
                        "Foliar nutrition support",
                        "Improve stress response and recovery",
                        2.5,
                        "L/ha",
                        NutritionInputStatus.RECOMMENDED
                )),
                new NutritionApplicationWindow(today, today.plusDays(3)),
                new PlanRationale(
                        "Plan generated from a HIGH climate risk.",
                        ClimateRiskLevel.HIGH,
                        new NdviValue(0.3),
                        4.5
                ),
                today
        );
        plan.restoreIdentity(new DynamicNutritionPlanId(1L));
        return plan;
    }

    private static Plot createPlot(Long ownerUserId, Long plotId) {
        var pointA = new GeoPoint(-12.0, -77.0);
        var polygon = new PolygonCoordinates(List.of(
                pointA,
                new GeoPoint(-12.0, -76.9),
                new GeoPoint(-12.1, -76.9),
                pointA
        ));
        var plot = new Plot(
                new UserId(ownerUserId),
                new PlotName("Santa Rosa"),
                polygon,
                new AreaSize(new BigDecimal("10.00")),
                "Olive",
                "Sevillana"
        );
        plot.restoreIdentity(new PlotId(plotId));
        return plot;
    }

    private static final class InMemoryDynamicNutritionPlanRepository implements DynamicNutritionPlanRepository {
        private DynamicNutritionPlan plan;

        @Override
        public Optional<DynamicNutritionPlan> findById(DynamicNutritionPlanId id) {
            return Optional.ofNullable(plan)
                    .filter(existingPlan -> existingPlan.getId().equals(id));
        }

        @Override
        public Optional<DynamicNutritionPlan> findActiveByUserIdAndPlotId(UserId userId, PlotId plotId) {
            return Optional.ofNullable(plan)
                    .filter(DynamicNutritionPlan::isActive)
                    .filter(existingPlan -> existingPlan.getUserId().equals(userId)
                            && existingPlan.getPlotId().equals(plotId));
        }

        @Override
        public DynamicNutritionPlan save(DynamicNutritionPlan dynamicNutritionPlan) {
            this.plan = dynamicNutritionPlan;
            return dynamicNutritionPlan;
        }
    }

    private static final class InMemoryPlotRepository implements PlotRepository {
        private Plot plot;

        @Override
        public Optional<Plot> findById(PlotId id) {
            return Optional.ofNullable(plot)
                    .filter(existingPlot -> existingPlot.getId().equals(id));
        }

        @Override
        public List<Plot> findAll() {
            return plot == null ? List.of() : List.of(plot);
        }

        @Override
        public List<Plot> findByUserId(UserId userId) {
            return new ArrayList<>(findAll());
        }

        @Override
        public Optional<Plot> findByNameAndUserId(PlotName name, UserId userId) {
            return Optional.empty();
        }

        @Override
        public Plot save(Plot plot) {
            this.plot = plot;
            return plot;
        }

        @Override
        public boolean existsById(PlotId id) {
            return findById(id).isPresent();
        }

        @Override
        public boolean existsByNameAndUserId(PlotName name, UserId userId) {
            return false;
        }

        @Override
        public boolean existsByNameAndUserIdAndIdIsNot(PlotName name, UserId userId, PlotId id) {
            return false;
        }

        @Override
        public boolean hasRelatedOperationalRecords(PlotId id) {
            return false;
        }

        @Override
        public void deleteById(PlotId id) {
            plot = null;
        }
    }
}
