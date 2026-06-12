package com.arcadiadevs.viora.platform.agronomic.interfaces.rest;

import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.repositories.SpringDataPlotRepository;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PolygonCoordinates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "springdoc.api-docs.enabled=false",
        "springdoc.swagger-ui.enabled=false"
})
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class PlotsDataSourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SpringDataPlotRepository plotRepository;

    private Long plotId;
    private BigDecimal initialCalculatedArea;

    @BeforeEach
    void setUp() throws Exception {
        var response = mockMvc.perform(post("/api/v1/plots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": 10,
                                  "name": "Original plot",
                                  "polygonCoordinates": [
                                    [-77.0, -12.0],
                                    [-76.9, -12.0],
                                    [-76.9, -12.1],
                                    [-77.0, -12.0]
                                  ],
                                  "cropType": "Olive",
                                  "variety": "Sevillana",
                                  "location": "Tacna, Peru",
                                  "campaign": "2026 campaign",
                                  "notes": "Regular irrigation."
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.state").value("enable"))
                .andExpect(jsonPath("$.areaSizeHectares").isNumber())
                .andExpect(jsonPath("$.estimatedAreaHectares").doesNotExist())
                .andExpect(jsonPath("$.climateMonitoring").value("NOT_LINKED"))
                .andExpect(jsonPath("$.satelliteNdvi").value("NOT_LINKED"))
                .andExpect(jsonPath("$.iotDevices").value("NOT_LINKED"))
                .andReturn();

        var location = response.getResponse().getContentAsString();
        plotId = Long.valueOf(location.replaceAll(".*\"id\":(\\d+).*", "$1"));
        initialCalculatedArea = areaOf(
                new GeoPoint(-12.0, -77.0),
                new GeoPoint(-12.0, -76.9),
                new GeoPoint(-12.1, -76.9)
        );
    }

    @Test
    void getPatchAndDeletePlotThroughJpaDataSource() throws Exception {
        mockMvc.perform(get("/api/v1/plots/{plotId}", plotId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(plotId))
                .andExpect(jsonPath("$.name").value("Original plot"))
                .andExpect(jsonPath("$.location").value("Tacna, Peru"))
                .andExpect(jsonPath("$.campaign").value("2026 campaign"))
                .andExpect(jsonPath("$.notes").value("Regular irrigation."));

        mockMvc.perform(get("/api/v1/plots/overview").param("userId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registeredPlotCount").value(1))
                .andExpect(jsonPath("$.monitoredAreaHectares").value(initialCalculatedArea.doubleValue()))
                .andExpect(jsonPath("$.climateLinkedPlotCount").value(0))
                .andExpect(jsonPath("$.onlineDeviceCount").value(0))
                .andExpect(jsonPath("$.plots[0].id").value(plotId))
                .andExpect(jsonPath("$.plots[0].location").value("Tacna, Peru"))
                .andExpect(jsonPath("$.plots[0].activeAlertCount").value(0))
                .andExpect(jsonPath("$.plots[0].healthStatus").value("UNKNOWN"))
                .andExpect(jsonPath("$.plots[0].climateMonitoring").value("NOT_LINKED"));

        mockMvc.perform(get("/api/v1/plots/{plotId}/detail", plotId)
                        .param("userId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(plotId))
                .andExpect(jsonPath("$.boundaryPointCount").value(3))
                .andExpect(jsonPath("$.boundaryStatus").value("VALIDATED"))
                .andExpect(jsonPath("$.registeredAt").isString())
                .andExpect(jsonPath("$.monitoringLinks.climateMonitoring")
                        .value("NOT_LINKED"))
                .andExpect(jsonPath("$.monitoringLinks.satelliteNdvi")
                        .value("NOT_LINKED"))
                .andExpect(jsonPath("$.iot.status").value("NOT_LINKED"))
                .andExpect(jsonPath("$.iot.linkedDeviceCount").value(0))
                .andExpect(jsonPath("$.recentConfigurationActivity[0].type")
                        .value("PLOT_REGISTERED"));

        mockMvc.perform(post("/api/v1/plots/{plotId}/iot-devices", plotId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "deviceName": "Soil moisture sensor",
                                  "status": "ACTIVE"
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/plots/{plotId}/detail", plotId)
                        .param("userId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.iot.status").value("ACTIVE"))
                .andExpect(jsonPath("$.iot.linkedDeviceCount").value(1))
                .andExpect(jsonPath("$.iot.onlineDeviceCount").value(1))
                .andExpect(jsonPath("$.iot.lastActivityAt").isString())
                .andExpect(jsonPath("$.devices[0].name").value("Soil moisture sensor"))
                .andExpect(jsonPath("$.devices[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$.devices[0].linkedAt").isString())
                .andExpect(jsonPath("$.recentConfigurationActivity[0].type")
                        .value("IOT_DEVICE_LINKED"));

        mockMvc.perform(get("/api/v1/plots").param("userId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(plotId));

        mockMvc.perform(get("/api/v1/plots")
                        .param("userId", "10")
                        .param("includeCurrentImagery", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(plotId))
                .andExpect(jsonPath("$[0].areaSize").value(initialCalculatedArea.doubleValue()))
                .andExpect(jsonPath("$[0].polygonCoordinates[0][0]").value(-77.0))
                .andExpect(jsonPath("$[0].polygonCoordinates[0][1]").value(-12.0))
                .andExpect(jsonPath("$[0].currentImagery").isEmpty());

        mockMvc.perform(patch("/api/v1/plots/{plotId}", plotId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Updated plot",
                                  "polygonCoordinates": [
                                    [-77.0, -12.0],
                                    [-76.99, -12.0],
                                    [-76.99, -12.01],
                                    [-77.0, -12.0]
                                  ],
                                  "location": "La Yarada, Tacna",
                                  "campaign": "2027 campaign",
                                  "notes": "Updated notes."
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated plot"))
                .andExpect(jsonPath("$.areaSizeHectares").value(
                        areaOf(
                                new GeoPoint(-12.0, -77.0),
                                new GeoPoint(-12.0, -76.99),
                                new GeoPoint(-12.01, -76.99)
                        ).doubleValue()
                ))
                .andExpect(jsonPath("$.location").value("La Yarada, Tacna"))
                .andExpect(jsonPath("$.campaign").value("2027 campaign"))
                .andExpect(jsonPath("$.notes").value("Updated notes."));

        var updatedEntity = plotRepository.findById(plotId).orElseThrow();
        assertEquals("Updated plot", updatedEntity.getName());
        assertEquals(
                areaOf(
                        new GeoPoint(-12.0, -77.0),
                        new GeoPoint(-12.0, -76.99),
                        new GeoPoint(-12.01, -76.99)
                ),
                updatedEntity.getAreaSize()
        );
        assertEquals("La Yarada, Tacna", updatedEntity.getLocation());
        assertEquals("2027 campaign", updatedEntity.getCampaign());
        assertEquals("Updated notes.", updatedEntity.getNotes());

        mockMvc.perform(delete("/api/v1/plots/{plotId}", plotId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Plot deleted successfully."));

        assertFalse(plotRepository.findById(plotId).orElseThrow().getActive());

        mockMvc.perform(get("/api/v1/plots/{plotId}", plotId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PLOT_NOT_FOUND"));
    }

    private BigDecimal areaOf(GeoPoint first, GeoPoint second, GeoPoint third) {
        return new PolygonCoordinates(java.util.List.of(first, second, third, first))
                .estimatedAreaHectares();
    }
}
