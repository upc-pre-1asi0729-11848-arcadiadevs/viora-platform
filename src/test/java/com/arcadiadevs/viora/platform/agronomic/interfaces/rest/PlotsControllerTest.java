package com.arcadiadevs.viora.platform.agronomic.interfaces.rest;

import com.arcadiadevs.viora.platform.agronomic.application.commandservices.PlotCommandService;
import com.arcadiadevs.viora.platform.agronomic.application.queryservices.PlotQueryService;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.DeletePlotCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.UpdatePlotCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotByIdQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AreaSize;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotName;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PolygonCoordinates;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.shared.application.result.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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

    @Test
    void getsPlotById() throws Exception {
        when(plotQueryService.handle(any(GetPlotByIdQuery.class)))
                .thenReturn(Result.success(createPlot()));

        mockMvc.perform(get("/api/v1/plots/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
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
                "Typica"
        );
        return plot.restoreIdentity(new PlotId(1L));
    }
}
