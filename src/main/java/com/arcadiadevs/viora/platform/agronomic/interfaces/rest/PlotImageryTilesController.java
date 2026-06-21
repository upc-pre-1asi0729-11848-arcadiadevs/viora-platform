package com.arcadiadevs.viora.platform.agronomic.interfaces.rest;

import com.arcadiadevs.viora.platform.agronomic.application.queryservices.PlotQueryService;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotNdviTileQuery;
import com.arcadiadevs.viora.platform.shared.interfaces.rest.transform.ErrorResponseAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

/**
 * Plot imagery tiles REST controller.
 *
 * <p>
 * Proxies raster NDVI tiles of the current satellite imagery of a plot from the
 * configured provider. The provider API key is applied server-side, so it is
 * never exposed to web clients.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/plots/{plotId}/images")
@RequiredArgsConstructor
@Tag(name = "Plots", description = "Plots Management Endpoints")
public class PlotImageryTilesController {

    private static final Duration TILE_CACHE_DURATION = Duration.ofMinutes(30);

    private final PlotQueryService plotQueryService;

    /**
     * Streams a raster NDVI tile of the current imagery for a plot.
     *
     * @param plotId The plot identifier.
     * @param zoom The web-map zoom level.
     * @param x The tile column.
     * @param y The tile row.
     * @param userId The owner user identifier.
     * @return The PNG tile bytes, or a standardized error response.
     */
    @GetMapping(produces = {IMAGE_PNG_VALUE, APPLICATION_JSON_VALUE})
    @Operation(
            summary = "Get current NDVI imagery tile",
            description = "Streams a raster NDVI tile of the plot's current satellite imagery "
                    + "through the platform, keeping provider credentials server-side."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "NDVI tile streamed"),
            @ApiResponse(responseCode = "400", description = "Invalid tile coordinates or parameters"),
            @ApiResponse(responseCode = "403", description = "Authenticated user does not own the plot"),
            @ApiResponse(responseCode = "404", description = "Plot not found or no imagery available"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    public ResponseEntity<?> getCurrentNdviTile(
            @Parameter(description = "Plot identifier", required = true)
            @PathVariable Long plotId,

            @Parameter(description = "Web-map zoom level", required = true)
            @RequestParam int zoom,

            @Parameter(description = "Tile column", required = true)
            @RequestParam int x,

            @Parameter(description = "Tile row", required = true)
            @RequestParam int y,

            @Parameter(description = "User identifier", required = true)
            @RequestParam Long userId
    ) {
        var query = new GetPlotNdviTileQuery(userId, plotId, zoom, x, y);
        var result = plotQueryService.handle(query);

        if (result.isFailure()) {
            var errorResponse = ErrorResponseAssembler.toErrorResponseFromApplicationError(
                    result.failure().orElseThrow());
            /* The JSON content type must be explicit: content negotiation would otherwise
               pick image/png from the mapping and fail to write the error resource. */
            return ResponseEntity.status(errorResponse.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse.getBody());
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .cacheControl(CacheControl.maxAge(TILE_CACHE_DURATION).cachePrivate())
                .body(result.success().orElseThrow());
    }
}
