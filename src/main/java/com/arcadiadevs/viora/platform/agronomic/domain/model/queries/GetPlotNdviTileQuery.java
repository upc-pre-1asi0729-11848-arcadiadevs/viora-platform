package com.arcadiadevs.viora.platform.agronomic.domain.model.queries;

/**
 * Query to retrieve a raster NDVI tile of the current imagery for a plot.
 *
 * @param userId The requesting owner user identifier.
 * @param plotId The plot identifier.
 * @param zoom The web-map zoom level.
 * @param x The tile column for the zoom level.
 * @param y The tile row for the zoom level.
 */
public record GetPlotNdviTileQuery(Long userId, Long plotId, int zoom, int x, int y) {

    private static final int MAX_ZOOM = 20;

    public GetPlotNdviTileQuery {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number.");
        }
        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException("Plot ID must be a positive number.");
        }
        if (zoom < 0 || zoom > MAX_ZOOM) {
            throw new IllegalArgumentException("Zoom must be between 0 and %d.".formatted(MAX_ZOOM));
        }
        long tilesPerAxis = 1L << zoom;
        if (x < 0 || x >= tilesPerAxis || y < 0 || y >= tilesPerAxis) {
            throw new IllegalArgumentException(
                    "Tile coordinates must be between 0 and %d for zoom %d.".formatted(tilesPerAxis - 1, zoom));
        }
    }
}
