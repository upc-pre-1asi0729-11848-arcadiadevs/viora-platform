package com.arcadiadevs.viora.platform.shared.infrastructure.documentation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "springdoc.api-docs.enabled=true",
        "springdoc.swagger-ui.enabled=true"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpenApiAvailabilityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void exposesOpenApiDocumentAndSwaggerUi() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/v1/plots']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/plots/overview']").exists())
                .andExpect(jsonPath(
                        "$.paths['/api/v1/plots'].get.parameters[?(@.name == 'includeCurrentImagery')]"
                ).exists())
                .andExpect(jsonPath("$.components.schemas.PlotWithCurrentImageryResource").exists())
                .andExpect(jsonPath("$.components.schemas.SatelliteImageryResource").exists())
                .andExpect(jsonPath("$.components.schemas.MyPlotsOverviewResource").exists())
                .andExpect(jsonPath("$.components.schemas.PlotRegistrationResource").exists())
                .andExpect(jsonPath("$.paths['/api/v1/plots/{plotId}']").exists())
                .andExpect(jsonPath(
                        "$.paths['/api/v1/plots/{plotId}/imagery/tile/{zoom}/{x}/{y}']"
                ).exists())
                .andExpect(jsonPath("$.paths['/api/v1/plots/{plotId}/iot-devices']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/agronomic-statistics']").exists());

        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
    }
}
