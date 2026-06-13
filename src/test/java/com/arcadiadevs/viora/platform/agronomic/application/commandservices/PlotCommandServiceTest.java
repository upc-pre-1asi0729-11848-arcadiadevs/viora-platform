package com.arcadiadevs.viora.platform.agronomic.application.commandservices;

import com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices.AgroMonitoringImageryService;
import com.arcadiadevs.viora.platform.agronomic.application.readmodels.IntegrationLinkStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.ConfigureChillRequirementCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.CreatePlotCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.DeletePlotCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.ResetChillRequirementCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.UpdatePlotCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.ChillRequirementResolver;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AreaSize;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillRequirementPolicy;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillRequirementSource;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotName;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PolygonCoordinates;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.SatelliteImagery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.PlotRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class PlotCommandServiceTest {

    private static final ChillRequirementResolver CHILL_RESOLVER = new ChillRequirementResolver(
            new ChillRequirementPolicy(50.0, Map.of("olive", 40.0)));

    // The initial-ingestion seeding is an after-commit side effect that never runs
    // in these plain unit tests (no active transaction), so a no-op mock suffices.
    private static final AgronomicStatisticIngestionService INGESTION_SERVICE =
            mock(AgronomicStatisticIngestionService.class);

    @Test
    void updatesPlotAfterAllInputIsValidated() {
        var repository = new InMemoryPlotRepository();
        repository.plot = createPlot();
        var service = new PlotCommandService(repository, new StubImageryService(false, false), INGESTION_SERVICE, CHILL_RESOLVER);
        var result = service.handle(new UpdatePlotCommand(
                1L,
                "North field",
                validCoordinates(),
                "Coffee",
                "Typica",
                "Tacna, Peru",
                "2026 campaign",
                "Updated notes"
        ));

        assertTrue(result.isSuccess());
        assertEquals("North field", repository.plot.getName().getValue());
        assertEquals(expectedArea(), repository.plot.getAreaSize().getHectares());
        assertEquals("Tacna, Peru", repository.plot.getLocation());
        assertEquals("2026 campaign", repository.plot.getCampaign());
        assertEquals("Updated notes", repository.plot.getNotes());
        assertEquals(1, repository.saveCount);
    }

    @Test
    void invalidPolygonDoesNotMutatePlot() {
        var repository = new InMemoryPlotRepository();
        repository.plot = createPlot();
        var service = new PlotCommandService(repository, new StubImageryService(false, false), INGESTION_SERVICE, CHILL_RESOLVER);

        var result = service.handle(new UpdatePlotCommand(
                1L,
                "Changed name",
                List.of(List.of(-12.0, -77.0)),
                null,
                null
        ));

        assertTrue(result.isFailure());
        assertEquals("Original plot", repository.plot.getName().getValue());
        assertEquals(0, repository.saveCount);
    }

    @Test
    void duplicateNameReturnsConflict() {
        var repository = new InMemoryPlotRepository();
        repository.plot = createPlot();
        repository.duplicateName = true;
        var service = new PlotCommandService(repository, new StubImageryService(false, false), INGESTION_SERVICE, CHILL_RESOLVER);

        var result = service.handle(new UpdatePlotCommand(
                1L,
                "Existing plot",
                null,
                null,
                null
        ));

        assertTrue(result.isFailure());
        assertEquals("PLOT_CONFLICT", result.failure().orElseThrow().code());
        assertEquals(0, repository.saveCount);
    }

    @Test
    void deleteUsesLogicalDeletionWhenRelatedRecordsExist() {
        var repository = new InMemoryPlotRepository();
        repository.plot = createPlot();
        repository.relatedRecords = true;
        var service = new PlotCommandService(repository, new StubImageryService(false, false), INGESTION_SERVICE, CHILL_RESOLVER);

        var result = service.handle(new DeletePlotCommand(1L));

        assertTrue(result.isSuccess());
        assertFalse(repository.plot.isActive());
        assertEquals(1, repository.saveCount);
        assertEquals(0, repository.deleteCount);
    }

    @Test
    void createsRegistrationWithEstimatedAreaAndIntegrationStates() {
        var repository = new InMemoryPlotRepository();
        var service = new PlotCommandService(
                repository,
                new StubImageryService(true, true),
                INGESTION_SERVICE,
                CHILL_RESOLVER
        );

        var result = service.handle(new CreatePlotCommand(
                10L,
                "Santa Rosa",
                validCoordinates(),
                "Olive",
                "Sevillana",
                "Tacna, Peru",
                "2026 campaign",
                "Regular irrigation."
        ));

        assertTrue(result.isSuccess());
        var registration = result.success().orElseThrow();
        assertEquals(expectedArea(), registration.plot().getAreaSize().getHectares());
        assertEquals(IntegrationLinkStatus.ACTIVE, registration.climateMonitoring());
        assertEquals(IntegrationLinkStatus.INITIALIZING, registration.satelliteNdvi());
        assertEquals(IntegrationLinkStatus.NOT_LINKED, registration.iotDevices());
    }

    @Test
    void configureChillRequirementStoresUserDeclaredOverride() {
        var repository = new InMemoryPlotRepository();
        repository.plot = createPlot();
        var service = new PlotCommandService(repository, new StubImageryService(false, false), INGESTION_SERVICE, CHILL_RESOLVER);

        var result = service.handle(new ConfigureChillRequirementCommand(1L, 10L, 35.0));

        assertTrue(result.isSuccess());
        var requirement = result.success().orElseThrow();
        assertEquals(35.0, requirement.value(), 1e-6);
        assertEquals(ChillRequirementSource.USER_DECLARED, requirement.source());
        assertEquals(ChillRequirementSource.USER_DECLARED, repository.plot.getChillRequirementOverride().source());
        assertEquals(1, repository.saveCount);
    }

    @Test
    void rejectsAbsurdChillRequirement() {
        var repository = new InMemoryPlotRepository();
        repository.plot = createPlot();
        var service = new PlotCommandService(repository, new StubImageryService(false, false), INGESTION_SERVICE, CHILL_RESOLVER);

        var result = service.handle(new ConfigureChillRequirementCommand(1L, 10L, 5000.0));

        assertTrue(result.isFailure());
        assertEquals("VALIDATION_ERROR", result.failure().orElseThrow().code());
        assertEquals(0, repository.saveCount);
    }

    @Test
    void resetChillRequirementRevertsToSystemDefault() {
        var repository = new InMemoryPlotRepository();
        repository.plot = createPlot();
        var service = new PlotCommandService(repository, new StubImageryService(false, false), INGESTION_SERVICE, CHILL_RESOLVER);
        service.handle(new ConfigureChillRequirementCommand(1L, 10L, 35.0));

        var result = service.handle(new ResetChillRequirementCommand(1L, 10L));

        assertTrue(result.isSuccess());
        var requirement = result.success().orElseThrow();
        // Cacao has no crop default, so it falls back to the neutral default (NOT_CONFIGURED).
        assertEquals(50.0, requirement.value(), 1e-6);
        assertEquals(ChillRequirementSource.NOT_CONFIGURED, requirement.source());
        assertNull(repository.plot.getChillRequirementOverride());
    }

    @Test
    void configureChillRequirementForbiddenWhenUserDoesNotOwnPlot() {
        var repository = new InMemoryPlotRepository();
        repository.plot = createPlot();
        var service = new PlotCommandService(repository, new StubImageryService(false, false), INGESTION_SERVICE, CHILL_RESOLVER);

        var result = service.handle(new ConfigureChillRequirementCommand(1L, 999L, 35.0));

        assertTrue(result.isFailure());
        assertEquals("PLOT_OWNERSHIP_FORBIDDEN", result.failure().orElseThrow().code());
        assertEquals(0, repository.saveCount);
    }

    private Plot createPlot() {
        var pointA = new GeoPoint(-12.0, -77.0);
        var polygon = new PolygonCoordinates(List.of(
                pointA,
                new GeoPoint(-12.0, -76.9),
                new GeoPoint(-12.1, -76.9),
                pointA
        ));
        var plot = new Plot(
                new UserId(10L),
                new PlotName("Original plot"),
                polygon,
                new AreaSize(new BigDecimal("10.00")),
                "Cacao",
                "Criollo"
        );
        plot.restoreIdentity(new PlotId(1L));
        return plot;
    }

    private List<List<Double>> validCoordinates() {
        return List.of(
                List.of(-77.0, -12.0),
                List.of(-76.9, -12.0),
                List.of(-76.9, -12.1),
                List.of(-77.0, -12.0)
        );
    }

    private BigDecimal expectedArea() {
        var pointA = new GeoPoint(-12.0, -77.0);
        return new PolygonCoordinates(List.of(
                pointA,
                new GeoPoint(-12.0, -76.9),
                new GeoPoint(-12.1, -76.9),
                pointA
        )).estimatedAreaHectares();
    }

    private static final class InMemoryPlotRepository implements PlotRepository {
        private Plot plot;
        private boolean duplicateName;
        private boolean relatedRecords;
        private int saveCount;
        private int deleteCount;

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
            if (plot.getId() == null) {
                plot.restoreIdentity(new PlotId(1L));
            }
            this.plot = plot;
            saveCount++;
            return plot;
        }

        @Override
        public boolean existsById(PlotId id) {
            return findById(id).isPresent();
        }

        @Override
        public boolean existsByNameAndUserId(PlotName name, UserId userId) {
            return duplicateName;
        }

        @Override
        public boolean existsByNameAndUserIdAndIdIsNot(
                PlotName name,
                UserId userId,
                PlotId id
        ) {
            return duplicateName;
        }

        @Override
        public boolean hasRelatedOperationalRecords(PlotId id) {
            return relatedRecords;
        }

        @Override
        public void deleteById(PlotId id) {
            plot = null;
            deleteCount++;
        }
    }

    private record StubImageryService(
            boolean enabled,
            boolean linked
    ) implements AgroMonitoringImageryService {

        @Override
        public boolean isIntegrationEnabled() {
            return enabled;
        }

        @Override
        public boolean isPlotLinked(Plot plot) {
            return linked;
        }

        @Override
        public Optional<SatelliteImagery> findCurrentImagery(Plot plot) {
            return Optional.empty();
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
}
