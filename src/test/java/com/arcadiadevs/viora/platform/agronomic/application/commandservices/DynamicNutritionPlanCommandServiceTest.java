package com.arcadiadevs.viora.platform.agronomic.application.commandservices;

import com.arcadiadevs.viora.platform.agronomic.application.internal.DynamicNutritionPlanAssemblerService;
import com.arcadiadevs.viora.platform.agronomic.application.internal.PlotOwnershipValidator;
import com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices.AgroMonitoringImageryService;
import com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices.WeatherDataService;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.AgronomicStatistic;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.DynamicNutritionPlan;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.CertifyNutritionApplicationCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.RecommendDynamicNutritionCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.ChillRequirementResolver;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.ChillSeasonEvaluator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.ClimateRiskEvaluator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.DynamicNutritionPlanGenerator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.PhenologicalRiskEvaluator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AccumulatedChillHours;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AreaSize;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillPortions;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillRequirementPolicy;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ClimateRiskLevel;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DateRange;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviValue;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DynamicNutritionPlanId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DynamicNutritionPolicy;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MeasurementDate;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NutritionPlanStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotName;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PolygonCoordinates;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.SatelliteImagery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherSnapshot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.AgronomicStatisticRepository;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.DynamicNutritionPlanRepository;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.PlotRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DynamicNutritionPlanCommandServiceTest {

    private static final Instant NOW = Instant.parse("2026-06-11T12:00:00Z");
    private static final Instant NDVI_CAPTURE = Instant.parse("2026-06-09T15:00:00Z");

    @Test
    void recommendsPlanFromProviderBackedSignalsWhenRiskIsHigh() {
        var fixture = new Fixture();
        fixture.plotRepository.plot = createPlot(10L, 1L);
        fixture.imageryService.imagery = imagery(0.20);

        var result = fixture.service().handle(new RecommendDynamicNutritionCommand(10L, 1L));

        assertTrue(result.isSuccess());
        var plan = result.success().orElseThrow();
        assertEquals(NutritionPlanStatus.ACTIVE, plan.getStatus());
        assertEquals(ClimateRiskLevel.HIGH, plan.getRationale().getTriggeringRiskLevel());
        assertEquals(LocalDate.of(2026, 6, 11), plan.getGeneratedDate());
        assertEquals(3, plan.getInputRecommendations().size());
        assertTrue(plan.getRationale().getSummary().contains("AgroMonitoring NDVI 0.20"));
        assertTrue(plan.getRationale().getSummary().contains("2026-06-09"));
        assertEquals(1, fixture.planRepository.saveCount);
    }

    @Test
    void recommendsPlanFromPhenologicalChillDeficitInSeasonWhenClimateIsLow() {
        var fixture = new Fixture();
        // Southern-hemisphere plot inside its chill window (Jul–Nov).
        fixture.clockNow = Instant.parse("2026-08-15T12:00:00Z");
        fixture.plotRepository.plot = createPlot(10L, 1L);
        fixture.imageryService.imagery = imagery(0.70); // healthy vigor → climate not high
        fixture.statisticRepository.latest = chillStatistic(5.0); // 5 / 40 = severe deficit

        var result = fixture.service().handle(new RecommendDynamicNutritionCommand(10L, 1L));

        assertTrue(result.isSuccess());
        assertEquals(
                ClimateRiskLevel.HIGH,
                result.success().orElseThrow().getRationale().getTriggeringRiskLevel());
    }

    @Test
    void supersedesPreviouslyActivePlan() {
        var fixture = new Fixture();
        fixture.plotRepository.plot = createPlot(10L, 1L);
        fixture.imageryService.imagery = imagery(0.20);

        var firstPlan = fixture.service()
                .handle(new RecommendDynamicNutritionCommand(10L, 1L))
                .success()
                .orElseThrow();

        var secondResult = fixture.service().handle(new RecommendDynamicNutritionCommand(10L, 1L));

        assertTrue(secondResult.isSuccess());
        var supersededPlan = fixture.planRepository.findById(firstPlan.getId()).orElseThrow();
        assertEquals(NutritionPlanStatus.SUPERSEDED, supersededPlan.getStatus());
        assertEquals(NutritionPlanStatus.ACTIVE, secondResult.success().orElseThrow().getStatus());
    }

    @Test
    void returnsBusinessRuleViolationWhenRiskDoesNotJustifyPlan() {
        var fixture = new Fixture();
        fixture.plotRepository.plot = createPlot(10L, 1L);
        fixture.imageryService.imagery = imagery(0.70);

        var result = fixture.service().handle(new RecommendDynamicNutritionCommand(10L, 1L));

        assertTrue(result.isFailure());
        assertEquals("BUSINESS_RULE_VIOLATION", result.failure().orElseThrow().code());
        assertEquals(0, fixture.planRepository.saveCount);
    }

    @Test
    void returnsBusinessRuleViolationWhenCurrentNdviIsUnavailable() {
        var fixture = new Fixture();
        fixture.plotRepository.plot = createPlot(10L, 1L);

        var result = fixture.service().handle(new RecommendDynamicNutritionCommand(10L, 1L));

        assertTrue(result.isFailure());
        assertEquals("BUSINESS_RULE_VIOLATION", result.failure().orElseThrow().code());
        assertTrue(result.failure().orElseThrow().details().contains("NDVI"));
    }

    @Test
    void returnsBusinessRuleViolationWhenCurrentWeatherIsUnavailable() {
        var fixture = new Fixture();
        fixture.plotRepository.plot = createPlot(10L, 1L);
        fixture.imageryService.imagery = imagery(0.20);
        fixture.weatherDataService.weather = Optional.empty();

        var result = fixture.service().handle(new RecommendDynamicNutritionCommand(10L, 1L));

        assertTrue(result.isFailure());
        assertEquals("BUSINESS_RULE_VIOLATION", result.failure().orElseThrow().code());
        assertTrue(result.failure().orElseThrow().details().contains("weather"));
    }

    @Test
    void returnsNotFoundWhenPlotDoesNotExist() {
        var result = new Fixture().service()
                .handle(new RecommendDynamicNutritionCommand(10L, 1L));

        assertTrue(result.isFailure());
        assertEquals("PLOT_NOT_FOUND", result.failure().orElseThrow().code());
    }

    @Test
    void returnsForbiddenWhenPlotIsNotOwnedByUser() {
        var fixture = new Fixture();
        fixture.plotRepository.plot = createPlot(99L, 1L);

        var result = fixture.service().handle(new RecommendDynamicNutritionCommand(10L, 1L));

        assertTrue(result.isFailure());
        assertEquals("PLOT_OWNERSHIP_FORBIDDEN", result.failure().orElseThrow().code());
    }

    @Test
    void certifiesActivePlanApplication() {
        var fixture = new Fixture();
        var plan = recommendedPlan(fixture);
        var appliedInput = plan.getInputRecommendations().getFirst().getValue();

        var result = fixture.service().handle(new CertifyNutritionApplicationCommand(
                10L,
                plan.getId().getValue(),
                LocalDate.of(2026, 6, 11),
                LocalTime.of(7, 30),
                List.of(appliedInput),
                "AS_RECOMMENDED",
                "Santa Rosa field crew",
                "Applied during stable morning conditions."
        ));

        assertTrue(result.isSuccess());
        var certified = result.success().orElseThrow();
        assertTrue(certified.isCertified());
        assertEquals(appliedInput, certified.getApplication().appliedInputs().getFirst());
        assertEquals("Santa Rosa field crew", certified.getApplication().fieldOperator());
    }

    @Test
    void returnsForbiddenWhenCertifyingPlanOfAnotherUser() {
        var fixture = new Fixture();
        var plan = recommendedPlan(fixture);

        var result = fixture.service().handle(new CertifyNutritionApplicationCommand(
                99L,
                plan.getId().getValue(),
                LocalDate.of(2026, 6, 11),
                LocalTime.of(7, 30),
                List.of(plan.getInputRecommendations().getFirst().getValue()),
                "AS_RECOMMENDED",
                "crew",
                null
        ));

        assertTrue(result.isFailure());
        assertTrue(result.failure().orElseThrow().code().endsWith("_FORBIDDEN"));
    }

    @Test
    void returnsNotFoundWhenCertifyingMissingPlan() {
        var result = new Fixture().service().handle(new CertifyNutritionApplicationCommand(
                10L,
                999L,
                LocalDate.of(2026, 6, 11),
                LocalTime.of(7, 30),
                List.of("Foliar nutrition support"),
                "AS_RECOMMENDED",
                "crew",
                null
        ));

        assertTrue(result.isFailure());
        assertTrue(result.failure().orElseThrow().code().endsWith("_NOT_FOUND"));
    }

    @Test
    void returnsValidationErrorWhenAppliedInputIsNotPartOfThePlan() {
        var fixture = new Fixture();
        var plan = recommendedPlan(fixture);

        var result = fixture.service().handle(new CertifyNutritionApplicationCommand(
                10L,
                plan.getId().getValue(),
                LocalDate.of(2026, 6, 11),
                LocalTime.of(7, 30),
                List.of("Unknown input not in the plan"),
                "AS_RECOMMENDED",
                "crew",
                null
        ));

        assertTrue(result.isFailure());
        assertEquals("VALIDATION_ERROR", result.failure().orElseThrow().code());
    }

    @Test
    void returnsBusinessRuleViolationWhenCertifyingTwice() {
        var fixture = new Fixture();
        var plan = recommendedPlan(fixture);
        var command = new CertifyNutritionApplicationCommand(
                10L,
                plan.getId().getValue(),
                LocalDate.of(2026, 6, 11),
                LocalTime.of(7, 30),
                List.of(plan.getInputRecommendations().getFirst().getValue()),
                "AS_RECOMMENDED",
                "crew",
                null
        );

        fixture.service().handle(command);
        var secondResult = fixture.service().handle(command);

        assertTrue(secondResult.isFailure());
        assertEquals("BUSINESS_RULE_VIOLATION", secondResult.failure().orElseThrow().code());
    }

    private DynamicNutritionPlan recommendedPlan(Fixture fixture) {
        fixture.plotRepository.plot = createPlot(10L, 1L);
        fixture.imageryService.imagery = imagery(0.20);
        return fixture.service()
                .handle(new RecommendDynamicNutritionCommand(10L, 1L))
                .success()
                .orElseThrow();
    }

    private static SatelliteImagery imagery(double ndvi) {
        return new SatelliteImagery(
                "image-1",
                "https://example.test/ndvi/{z}/{x}/{y}",
                NDVI_CAPTURE,
                ndvi,
                2.0
        );
    }

    private static Plot createPlot(Long ownerUserId, Long plotId) {
        var pointA = new GeoPoint(-12.0, -77.0);
        var polygon = new PolygonCoordinates(List.of(
                pointA,
                new GeoPoint(-12.0, -76.9),
                new GeoPoint(-12.1, -76.9),
                pointA
        ));
        return new Plot(
                new UserId(ownerUserId),
                new PlotName("Santa Rosa"),
                polygon,
                new AreaSize(new BigDecimal("10.00")),
                "Olive",
                "Sevillana"
        ).restoreIdentity(new PlotId(plotId));
    }

    private static DynamicNutritionPolicy policy() {
        return new DynamicNutritionPolicy(20.0, 0.30, 0.50, 3, 2, 2.5, 3.0, 1.2);
    }

    private static final class Fixture {
        private final InMemoryPlotRepository plotRepository = new InMemoryPlotRepository();
        private final StubImageryService imageryService = new StubImageryService();
        private final StubWeatherDataService weatherDataService = new StubWeatherDataService();
        private final StubStatisticRepository statisticRepository = new StubStatisticRepository();
        private final InMemoryDynamicNutritionPlanRepository planRepository =
                new InMemoryDynamicNutritionPlanRepository();
        private Instant clockNow = NOW;

        private DynamicNutritionPlanCommandService service() {
            var assemblerService = new DynamicNutritionPlanAssemblerService(
                    imageryService,
                    weatherDataService,
                    statisticRepository,
                    new ClimateRiskEvaluator(),
                    new PhenologicalRiskEvaluator(),
                    new ChillSeasonEvaluator(),
                    new ChillRequirementResolver(new ChillRequirementPolicy(50.0, Map.of("olive", 40.0))),
                    new DynamicNutritionPlanGenerator(),
                    policy(),
                    Clock.fixed(clockNow, ZoneOffset.UTC)
            );
            return new DynamicNutritionPlanCommandService(
                    new PlotOwnershipValidator(plotRepository),
                    planRepository,
                    assemblerService
            );
        }
    }

    private static AgronomicStatistic chillStatistic(double chillPortions) {
        return new AgronomicStatistic(
                new UserId(10L),
                new PlotId(1L),
                new MeasurementDate(LocalDate.of(2026, 8, 1)),
                new NdviValue(0.70),
                new ChillPortions(chillPortions),
                new AccumulatedChillHours(0.0)
        );
    }

    private static final class StubStatisticRepository implements AgronomicStatisticRepository {
        private AgronomicStatistic latest;

        @Override
        public List<AgronomicStatistic> findAllByUserIdAndMeasurementDateBetween(
                UserId userId, DateRange dateRange) {
            return List.of();
        }

        @Override
        public List<AgronomicStatistic> findAllByUserIdAndPlotIdAndMeasurementDateBetween(
                UserId userId, PlotId plotId, DateRange dateRange) {
            return latest == null ? List.of() : List.of(latest);
        }

        @Override
        public Optional<AgronomicStatistic> findByPlotIdAndMeasurementDate(
                PlotId plotId, MeasurementDate measurementDate) {
            return Optional.empty();
        }

        @Override
        public Optional<AgronomicStatistic> findLatestByPlotId(PlotId plotId) {
            return Optional.ofNullable(latest);
        }

        @Override
        public AgronomicStatistic save(AgronomicStatistic agronomicStatistic) {
            latest = agronomicStatistic;
            return agronomicStatistic;
        }
    }

    private static final class StubImageryService implements AgroMonitoringImageryService {
        private SatelliteImagery imagery;

        @Override
        public boolean isIntegrationEnabled() {
            return true;
        }

        @Override
        public boolean isPlotLinked(Plot plot) {
            return imagery != null;
        }

        @Override
        public Optional<SatelliteImagery> findCurrentImagery(Plot plot) {
            return Optional.ofNullable(imagery);
        }

        @Override
        public Optional<byte[]> fetchCurrentNdviTile(Plot plot, int zoom, int x, int y) {
            return Optional.empty();
        }

        @Override
        public Optional<com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviHistory> findNdviHistory(
                Plot plot,
                com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DateRange range
        ) {
            return Optional.empty();
        }

        @Override
        public com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DataSourceMetadata describeNdviSource(Plot plot) {
            return com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DataSourceMetadata.notConfigured("AgroMonitoring");
        }
    }

    private static final class StubWeatherDataService implements WeatherDataService {
        private Optional<WeatherSnapshot> weather = Optional.of(new WeatherSnapshot(
                WeatherStatus.SUNNY,
                new MeasurementDate(LocalDate.of(2026, 6, 11)),
                ClimateRiskLevel.LOW,
                24.0
        ));

        @Override
        public Optional<WeatherSnapshot> getCurrentWeatherSnapshot(Plot plot) {
            return weather;
        }

        @Override
        public Optional<com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherForecast> getForecast(Plot plot) {
            return Optional.empty();
        }

        @Override
        public Optional<com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherHistory> getWeatherHistory(
                Plot plot,
                com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DateRange range
        ) {
            return Optional.empty();
        }

        @Override
        public com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DataSourceMetadata describeSource(Plot plot) {
            return com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DataSourceMetadata.notConfigured("AgroMonitoring");
        }
    }

    static final class InMemoryDynamicNutritionPlanRepository implements DynamicNutritionPlanRepository {
        private final List<DynamicNutritionPlan> plans = new ArrayList<>();
        private long nextId = 1;
        int saveCount;

        @Override
        public Optional<DynamicNutritionPlan> findById(DynamicNutritionPlanId id) {
            return plans.stream()
                    .filter(plan -> plan.getId().equals(id))
                    .findFirst();
        }

        @Override
        public Optional<DynamicNutritionPlan> findActiveByUserIdAndPlotId(UserId userId, PlotId plotId) {
            return plans.stream()
                    .filter(DynamicNutritionPlan::isActive)
                    .filter(plan -> plan.getUserId().equals(userId) && plan.getPlotId().equals(plotId))
                    .findFirst();
        }

        @Override
        public DynamicNutritionPlan save(DynamicNutritionPlan dynamicNutritionPlan) {
            saveCount++;
            if (dynamicNutritionPlan.getId() == null) {
                dynamicNutritionPlan.restoreIdentity(new DynamicNutritionPlanId(nextId++));
                plans.add(dynamicNutritionPlan);
            }
            return dynamicNutritionPlan;
        }
    }

    static final class InMemoryPlotRepository implements PlotRepository {
        Plot plot;

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
