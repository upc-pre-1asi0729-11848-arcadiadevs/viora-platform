package com.arcadiadevs.viora.platform.agronomic.interfaces.rest;

import com.arcadiadevs.viora.platform.agronomic.application.commandservices.PlotCommandService;
import com.arcadiadevs.viora.platform.agronomic.application.queryservices.PlotDetailQueryService;
import com.arcadiadevs.viora.platform.agronomic.application.queryservices.PlotMonitoringSummaryQueryService;
import com.arcadiadevs.viora.platform.agronomic.application.queryservices.PlotQueryService;
import com.arcadiadevs.viora.platform.agronomic.application.queryservices.PlotWeatherForecastQueryService;
import com.arcadiadevs.viora.platform.agronomic.application.readmodels.IntegrationLinkStatus;
import com.arcadiadevs.viora.platform.agronomic.application.readmodels.MyPlotsOverview;
import com.arcadiadevs.viora.platform.agronomic.application.readmodels.PlotMonitoringOverview;
import com.arcadiadevs.viora.platform.agronomic.application.readmodels.PlotDetail;
import com.arcadiadevs.viora.platform.agronomic.application.readmodels.PlotRegistration;
import com.arcadiadevs.viora.platform.agronomic.application.readmodels.PlotWithCurrentImagery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.CreatePlotCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.DeletePlotCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.UpdatePlotCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetMyPlotsOverviewQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotByIdQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotDetailQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotsWithCurrentImageryQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AreaSize;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ClimateRiskLevel;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeneralHealthStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotName;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PolygonCoordinates;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.SatelliteImagery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.shared.application.result.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlotsController.class)
class PlotsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlotQueryService plotQueryService;

    @MockitoBean
    private PlotCommandService plotCommandService;

    @MockitoBean
    private PlotDetailQueryService plotDetailQueryService;

    @MockitoBean
    private PlotMonitoringSummaryQueryService plotMonitoringSummaryQueryService;

    @MockitoBean
    private PlotWeatherForecastQueryService plotWeatherForecastQueryService;

    @Test
    void createsPlotWithEstimatedAreaAndInitialLinks() throws Exception {
        var plot = createPlot();
        var registration = new PlotRegistration(
                plot,
                IntegrationLinkStatus.ACTIVE,
                IntegrationLinkStatus.INITIALIZING,
                IntegrationLinkStatus.NOT_LINKED
        );
        when(plotCommandService.handle(any(CreatePlotCommand.class)))
                .thenReturn(Result.success(registration));

        mockMvc.perform(post("/api/v1/plots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": 10,
                                  "name": "North field",
                                  "polygonCoordinates": [
                                    [-77.0, -12.0],
                                    [-76.9, -12.0],
                                    [-76.9, -12.1],
                                    [-77.0, -12.0]
                                  ],
                                  "cropType": "Coffee",
                                  "variety": "Typica",
                                  "location": "Tacna, Peru",
                                  "campaign": "2026 campaign",
                                  "notes": "Regular irrigation."
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.areaSizeHectares").value(12.50))
                .andExpect(jsonPath("$.estimatedAreaHectares").doesNotExist())
                .andExpect(jsonPath("$.location").value("Tacna, Peru"))
                .andExpect(jsonPath("$.climateMonitoring").value("ACTIVE"))
                .andExpect(jsonPath("$.satelliteNdvi").value("INITIALIZING"))
                .andExpect(jsonPath("$.iotDevices").value("NOT_LINKED"));
    }

    @Test
    void getsPlotById() throws Exception {
        when(plotQueryService.handle(any(GetPlotByIdQuery.class)))
                .thenReturn(Result.success(createPlot()));

        mockMvc.perform(get("/api/v1/plots/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getsPlotsWithCurrentImagery() throws Exception {
        var plot = createPlot();
        var imagery = new SatelliteImagery(
                "image-1",
                "https://api.agromonitoring.com/tile/1.0/{z}/{x}/{y}/ndvi/image-1?appid=test",
                Instant.parse("2026-05-02T00:00:00Z"),
                0.62,
                2.5
        );
        when(plotQueryService.handle(any(GetPlotsWithCurrentImageryQuery.class)))
                .thenReturn(Result.success(List.of(
                        new PlotWithCurrentImagery(plot, java.util.Optional.of(imagery))
                )));

        mockMvc.perform(get("/api/v1/plots")
                        .param("userId", "10")
                        .param("includeCurrentImagery", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].areaSize").value(12.50))
                .andExpect(jsonPath("$[0].polygonCoordinates[0][0]").value(-77.0))
                .andExpect(jsonPath("$[0].polygonCoordinates[0][1]").value(-12.0))
                .andExpect(jsonPath("$[0].currentImagery.plotId").value(1))
                .andExpect(jsonPath("$[0].currentImagery.tileUrl").value(
                        "/api/v1/plots/1/imagery/tile/{z}/{x}/{y}?userId=10"
                ))
                .andExpect(jsonPath("$[0].currentImagery.ndviMean").value(0.62))
                .andExpect(jsonPath("$[0].currentImagery.cloudPercentage").value(2.5));
    }

    @Test
    void getsMyPlotsOverview() throws Exception {
        var plot = createPlot();
        var plotOverview = new PlotMonitoringOverview(
                plot,
                0.68,
                72.0,
                GeneralHealthStatus.HEALTHY,
                ClimateRiskLevel.LOW,
                2,
                0,
                Instant.parse("2026-06-11T12:00:00Z"),
                IntegrationLinkStatus.ACTIVE,
                IntegrationLinkStatus.ACTIVE
        );
        when(plotQueryService.handle(any(GetMyPlotsOverviewQuery.class)))
                .thenReturn(Result.success(new MyPlotsOverview(
                        1,
                        new BigDecimal("12.50"),
                        1,
                        2,
                        List.of(plotOverview)
                )));

        mockMvc.perform(get("/api/v1/plots/overview").param("userId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registeredPlotCount").value(1))
                .andExpect(jsonPath("$.monitoredAreaHectares").value(12.50))
                .andExpect(jsonPath("$.climateLinkedPlotCount").value(1))
                .andExpect(jsonPath("$.onlineDeviceCount").value(2))
                .andExpect(jsonPath("$.plots[0].name").value("North field"))
                .andExpect(jsonPath("$.plots[0].location").value("Tacna, Peru"))
                .andExpect(jsonPath("$.plots[0].currentNdvi").value(0.68))
                .andExpect(jsonPath("$.plots[0].chillPortions").value(72.0))
                .andExpect(jsonPath("$.plots[0].healthStatus").value("HEALTHY"))
                .andExpect(jsonPath("$.plots[0].phenologicalRisk").value("LOW"));
    }

    @Test
    void getsPlotDetail() throws Exception {
        var plot = createPlot();
        var detail = new PlotDetail(
                plot,
                Instant.parse("2026-05-01T15:00:00Z"),
                Instant.parse("2026-05-03T12:00:00Z"),
                "VALIDATED",
                IntegrationLinkStatus.ACTIVE,
                IntegrationLinkStatus.INITIALIZING,
                Instant.parse("2026-06-11T11:00:00Z"),
                Instant.parse("2026-06-11T11:00:00Z"),
                IntegrationLinkStatus.NOT_LINKED,
                0,
                null,
                List.of(),
                List.of(new PlotDetail.ConfigurationActivity(
                        "PLOT_REGISTERED",
                        "Plot boundary registered.",
                        Instant.parse("2026-05-01T15:00:00Z")
                ))
        );
        when(plotDetailQueryService.handle(any(GetPlotDetailQuery.class)))
                .thenReturn(Result.success(detail));

        mockMvc.perform(get("/api/v1/plots/1/detail").param("userId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("North field"))
                .andExpect(jsonPath("$.boundaryPointCount").value(3))
                .andExpect(jsonPath("$.boundaryStatus").value("VALIDATED"))
                .andExpect(jsonPath("$.monitoringLinks.climateMonitoring").value("ACTIVE"))
                .andExpect(jsonPath("$.monitoringLinks.satelliteNdvi").value("INITIALIZING"))
                .andExpect(jsonPath("$.iot.status").value("NOT_LINKED"))
                .andExpect(jsonPath("$.recentConfigurationActivity[0].type")
                        .value("PLOT_REGISTERED"));
    }

    @Test
    void getsPlotMonitoringSummary() throws Exception {
        var plot = createPlot();
        var summary = new com.arcadiadevs.viora.platform.agronomic.application.readmodels.PlotMonitoringSummary(
                plot,
                0.62,
                new com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviTrend(
                        com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviTrendDirection.RISING,
                        0.22,
                        List.of(new com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviStatistic(
                                Instant.parse("2026-06-01T00:00:00Z"), 0.62, null, null, null, null, null, null))
                ),
                45.0,
                new com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillRequirement(
                        new com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillPortions(40.0),
                        com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillRequirementSource.SYSTEM_DEFAULT,
                        com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillMetricModel.DYNAMIC),
                GeneralHealthStatus.HEALTHY,
                ClimateRiskLevel.MODERATE,
                42.0,
                null,
                com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ClimateRiskLevel.LOW,
                Instant.parse("2026-06-11T00:00:00Z"),
                List.of(),
                new com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DataSourceMetadata(
                        "AgroMonitoring",
                        com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ProviderDataAvailability.AVAILABLE,
                        null, 60),
                new com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DataSourceMetadata(
                        "AgroMonitoring",
                        com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ProviderDataAvailability.AVAILABLE,
                        Instant.parse("2026-06-10T00:00:00Z"), 60)
        );
        when(plotMonitoringSummaryQueryService.handle(
                any(com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotMonitoringSummaryQuery.class)))
                .thenReturn(Result.success(summary));

        mockMvc.perform(get("/api/v1/plots/1/monitoring-summary").param("userId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plotId").value(1))
                .andExpect(jsonPath("$.currentNdvi").value(0.62))
                .andExpect(jsonPath("$.ndviTrend.direction").value("RISING"))
                .andExpect(jsonPath("$.healthStatus").value("HEALTHY"))
                .andExpect(jsonPath("$.phenologicalRisk").value("MODERATE"))
                .andExpect(jsonPath("$.yieldForecastTonnes").value(42.0))
                .andExpect(jsonPath("$.climateRiskLevel").value("LOW"))
                .andExpect(jsonPath("$.ndviSource.availability").value("AVAILABLE"))
                .andExpect(jsonPath("$.ndviSource.updateFrequencyMinutes").value(60));
    }

    @Test
    void getsPlotWeatherForecast() throws Exception {
        var plot = createPlot();
        var forecast = new com.arcadiadevs.viora.platform.agronomic.application.readmodels.PlotWeatherForecast(
                plot,
                Instant.parse("2026-06-11T00:00:00Z"),
                List.of(),
                List.of(new com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DailyWeather(
                        java.time.LocalDate.of(2026, 6, 11), 1.5, 30.0, 18.0,
                        com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherStatus.SUNNY,
                        55, 0.0, 6.0)),
                1.5,
                com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ClimateRiskLevel.HIGH,
                List.of(new com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AgronomicWeatherWarning(
                        com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherWarningType.FROST,
                        com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ClimateRiskLevel.HIGH,
                        java.time.LocalDate.of(2026, 6, 11),
                        "Frost risk: minimum temperature 1.5 C.")),
                new com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DataSourceMetadata(
                        "AgroMonitoring",
                        com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ProviderDataAvailability.AVAILABLE,
                        Instant.parse("2026-06-11T00:00:00Z"), 60)
        );
        when(plotWeatherForecastQueryService.handle(
                any(com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotWeatherForecastQuery.class)))
                .thenReturn(Result.success(forecast));

        mockMvc.perform(get("/api/v1/plots/1/weather-forecast").param("userId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plotId").value(1))
                .andExpect(jsonPath("$.overallRisk").value("HIGH"))
                .andExpect(jsonPath("$.thermalAnomalyCelsius").value(1.5))
                .andExpect(jsonPath("$.daily[0].minTemperatureCelsius").value(1.5))
                .andExpect(jsonPath("$.warnings[0].type").value("FROST"))
                .andExpect(jsonPath("$.source.availability").value("AVAILABLE"));
    }

    @Test
    void updatesPlot() throws Exception {
        when(plotCommandService.handle(any(UpdatePlotCommand.class)))
                .thenReturn(Result.success(createPlot()));

        mockMvc.perform(patch("/api/v1/plots/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "North field"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deletesPlot() throws Exception {
        when(plotCommandService.handle(any(DeletePlotCommand.class)))
                .thenReturn(Result.success("Plot deleted successfully."));

        mockMvc.perform(delete("/api/v1/plots/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Plot deleted successfully."));
    }

    @Test
    void rejectsInvalidUpdateRequest() throws Exception {
        mockMvc.perform(patch("/api/v1/plots/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void rejectsInvalidPlotIdType() throws Exception {
        mockMvc.perform(get("/api/v1/plots/invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    private Plot createPlot() {
        var firstPoint = new GeoPoint(-12.0, -77.0);
        var plot = new Plot(
                new UserId(10L),
                new PlotName("North field"),
                new PolygonCoordinates(List.of(
                        firstPoint,
                        new GeoPoint(-12.0, -76.9),
                        new GeoPoint(-12.1, -76.9),
                        firstPoint
                )),
                new AreaSize(new BigDecimal("12.50")),
                "Coffee",
                "Typica",
                "Tacna, Peru",
                "2026 campaign",
                "Regular irrigation."
        );
        return plot.restoreIdentity(new PlotId(1L));
    }
}
