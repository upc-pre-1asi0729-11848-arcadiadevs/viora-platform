package com.arcadiadevs.viora.platform.agronomic.interfaces.rest;

import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.entities.AgronomicStatisticEntity;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.repositories.SpringDataAgronomicStatisticRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AgronomicEndpointsDataSourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SpringDataAgronomicStatisticRepository statisticRepository;

    private Long plotId;

    @BeforeEach
    void setUp() throws Exception {
        var response = mockMvc.perform(post("/api/v1/plots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": 20,
                                  "name": "IoT integration plot",
                                  "polygonCoordinates": [
                                    [-77.0, -12.0],
                                    [-76.9, -12.0],
                                    [-76.9, -12.1],
                                    [-77.0, -12.0]
                                  ],
                                  "cropType": "Olive",
                                  "variety": "Sevillana"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        plotId = Long.valueOf(response.getResponse().getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1"));
    }

    @Test
    void managesIoTDevicesThroughThePlotBoundary() throws Exception {
        var createResponse = mockMvc.perform(post(
                                "/api/v1/plots/{plotId}/iot-devices",
                                plotId
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "deviceName": "Soil sensor",
                                  "status": "ACTIVE"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.plotId").value(plotId))
                .andReturn();

        var deviceId = Long.valueOf(createResponse.getResponse().getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1"));

        mockMvc.perform(get("/api/v1/plots/{plotId}/iot-devices", plotId)
                        .param("userId", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(deviceId));

        mockMvc.perform(patch(
                                "/api/v1/plots/{plotId}/iot-devices/{deviceId}",
                                plotId,
                                deviceId
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "deviceName": "Updated soil sensor",
                                  "iotDeviceStatus": "WARNING"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deviceName").value("Updated soil sensor"))
                .andExpect(jsonPath("$.status").value("WARNING"));

        mockMvc.perform(delete(
                        "/api/v1/plots/{plotId}/iot-devices/{deviceId}",
                        plotId,
                        deviceId
                ))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/plots/{plotId}/iot-devices", plotId)
                        .param("userId", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void returnsAgronomicStatisticsAndEnforcesOwnership() throws Exception {
        var statistic = new AgronomicStatisticEntity();
        statistic.setUserId(20L);
        statistic.setPlotId(plotId);
        statistic.setMeasurementDate(LocalDate.now());
        statistic.setNdviValue(0.72);
        statistic.setChillPortions(18.0);
        statistic.setChillHours(245.0);
        statisticRepository.saveAndFlush(statistic);

        mockMvc.perform(get("/api/v1/agronomic-statistics")
                        .param("userId", "20")
                        .param("plotId", plotId.toString())
                        .param("timeRange", "LAST_7_DAYS")
                        .header("X-Authenticated-User-Id", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ndviValue").value(0.72));

        mockMvc.perform(get("/api/v1/agronomic-statistics")
                        .param("userId", "20")
                        .param("timeRange", "LAST_7_DAYS")
                        .header("X-Authenticated-User-Id", "99"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("AGRONOMIC_STATISTICS_ACCESS_FORBIDDEN"));
    }
}
