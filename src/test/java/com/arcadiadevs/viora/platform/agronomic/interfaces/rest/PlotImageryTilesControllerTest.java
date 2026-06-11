package com.arcadiadevs.viora.platform.agronomic.interfaces.rest;

import com.arcadiadevs.viora.platform.agronomic.application.queryservices.PlotQueryService;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotNdviTileQuery;
import com.arcadiadevs.viora.platform.shared.application.result.ApplicationError;
import com.arcadiadevs.viora.platform.shared.application.result.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlotImageryTilesController.class)
class PlotImageryTilesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlotQueryService plotQueryService;

    @Test
    void streamsPngTileThroughPlatformProxy() throws Exception {
        var tileBytes = new byte[]{(byte) 0x89, 'P', 'N', 'G'};
        when(plotQueryService.handle(any(GetPlotNdviTileQuery.class)))
                .thenReturn(Result.success(tileBytes));

        mockMvc.perform(get("/api/v1/plots/1/imagery/tile/12/1180/2122")
                        .param("userId", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(tileBytes))
                .andExpect(header().string(
                        HttpHeaders.CACHE_CONTROL,
                        "max-age=1800, private"
                ));
    }

    @Test
    void returnsJsonWhenCurrentImageryTileDoesNotExist() throws Exception {
        when(plotQueryService.handle(any(GetPlotNdviTileQuery.class)))
                .thenReturn(Result.failure(ApplicationError.notFound(
                        "plot_imagery_tile",
                        "1"
                )));

        mockMvc.perform(get("/api/v1/plots/1/imagery/tile/12/1180/2122")
                        .param("userId", "10"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("PLOT_IMAGERY_TILE_NOT_FOUND"));
    }

    @Test
    void rejectsCoordinatesOutsideTheRequestedZoom() throws Exception {
        mockMvc.perform(get("/api/v1/plots/1/imagery/tile/2/4/0")
                        .param("userId", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }
}
