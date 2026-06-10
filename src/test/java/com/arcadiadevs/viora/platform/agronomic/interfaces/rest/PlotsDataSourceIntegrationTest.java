package com.arcadiadevs.viora.platform.agronomic.interfaces.rest;

import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.repositories.SpringDataPlotRepository;
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
                                  "areaSizeHectares": 10.00,
                                  "cropType": "Olive",
                                  "variety": "Sevillana"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.state").value("enable"))
                .andReturn();

        var location = response.getResponse().getContentAsString();
        plotId = Long.valueOf(location.replaceAll(".*\"id\":(\\d+).*", "$1"));
    }

    @Test
    void getPatchAndDeletePlotThroughJpaDataSource() throws Exception {
        mockMvc.perform(get("/api/v1/plots/{plotId}", plotId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(plotId))
                .andExpect(jsonPath("$.name").value("Original plot"));

        mockMvc.perform(get("/api/v1/plots").param("userId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(plotId));

        mockMvc.perform(get("/api/v1/plots")
                        .param("userId", "10")
                        .param("includeCurrentImagery", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(plotId))
                .andExpect(jsonPath("$[0].areaSize").value(10.00))
                .andExpect(jsonPath("$[0].polygonCoordinates[0][0]").value(-77.0))
                .andExpect(jsonPath("$[0].polygonCoordinates[0][1]").value(-12.0))
                .andExpect(jsonPath("$[0].currentImagery").isEmpty());

        mockMvc.perform(patch("/api/v1/plots/{plotId}", plotId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Updated plot",
                                  "areaSizeHectares": 12.50
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated plot"))
                .andExpect(jsonPath("$.areaSizeHectares").value(12.50));

        var updatedEntity = plotRepository.findById(plotId).orElseThrow();
        assertEquals("Updated plot", updatedEntity.getName());
        assertEquals(new BigDecimal("12.50"), updatedEntity.getAreaSize());

        mockMvc.perform(delete("/api/v1/plots/{plotId}", plotId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Plot deleted successfully."));

        assertFalse(plotRepository.findById(plotId).orElseThrow().getActive());

        mockMvc.perform(get("/api/v1/plots/{plotId}", plotId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PLOT_NOT_FOUND"));
    }
}
