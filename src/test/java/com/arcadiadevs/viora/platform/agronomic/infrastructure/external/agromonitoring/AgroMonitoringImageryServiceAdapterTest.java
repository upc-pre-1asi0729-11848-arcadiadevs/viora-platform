package com.arcadiadevs.viora.platform.agronomic.infrastructure.external.agromonitoring;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AreaSize;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotName;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PolygonCoordinates;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class AgroMonitoringImageryServiceAdapterTest {

    @Test
    void returnsEmptyWithoutCallingPersistenceWhenIntegrationIsDisabled() {
        var properties = new AgroMonitoringProperties();
        var repository = mock(SpringDataAgroMonitoringPlotIntegrationRepository.class);
        var adapter = new AgroMonitoringImageryServiceAdapter(
                properties,
                repository,
                RestClient.create()
        );

        var result = adapter.findCurrentImagery(createPlot());

        assertTrue(result.isEmpty());
        verify(repository, never()).findByPlotId(any());
    }

    @Test
    void registersGeoJsonPolygonAndCachesLatestNdviImagery() {
        var properties = configuredProperties();
        var repository = mock(SpringDataAgroMonitoringPlotIntegrationRepository.class);
        when(repository.findByPlotId(1L)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var restClientBuilder = RestClient.builder();
        var server = MockRestServiceServer.bindTo(restClientBuilder).build();
        var adapter = new AgroMonitoringImageryServiceAdapter(
                properties,
                repository,
                restClientBuilder.baseUrl(properties.getBaseUrl()).build()
        );

        server.expect(once(), request -> {
                    assertEquals("/agro/1.0/polygons", request.getURI().getPath());
                    assertTrue(request.getURI().getQuery().contains("appid=test-key"));
                })
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                          "name": "North field",
                          "geo_json": {
                            "type": "Feature",
                            "properties": {},
                            "geometry": {
                              "type": "Polygon",
                              "coordinates": [[
                                [-77.0, -12.0],
                                [-76.9, -12.0],
                                [-76.9, -12.1],
                                [-77.0, -12.0]
                              ]]
                            }
                          }
                        }
                        """))
                .andRespond(withSuccess(
                        "{\"id\":\"provider-polygon-1\"}",
                        MediaType.APPLICATION_JSON
                ));

        server.expect(once(), request -> {
                    assertEquals("/agro/1.0/image/search", request.getURI().getPath());
                    var query = request.getURI().getQuery();
                    assertTrue(query.contains("polyid=provider-polygon-1"));
                    assertTrue(query.contains("appid=test-key"));
                })
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        [
                          {
                            "dt": 1770000000,
                            "cl": 8.5,
                            "tile": {
                              "ndvi": "http://api.agromonitoring.com/tile/1.0/{z}/{x}/{y}/old?appid=old"
                            },
                            "stats": {
                              "ndvi": "http://api.agromonitoring.com/stats/1.0/old?appid=old"
                            }
                          },
                          {
                            "dt": 1771000000,
                            "cl": 3.2,
                            "tile": {
                              "ndvi": "http://api.agromonitoring.com/tile/1.0/{z}/{x}/{y}/latest?appid=old"
                            },
                            "stats": {
                              "ndvi": "http://api.agromonitoring.com/stats/1.0/latest?appid=old"
                            }
                          }
                        ]
                        """, MediaType.APPLICATION_JSON));

        server.expect(once(), request -> {
                    assertEquals("/stats/1.0/latest", request.getURI().getPath());
                    assertEquals("appid=test-key", request.getURI().getQuery());
                })
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"mean\":0.64}", MediaType.APPLICATION_JSON));

        var result = adapter.findCurrentImagery(createPlot()).orElseThrow();

        assertEquals("1771000000", result.id());
        assertEquals(Instant.ofEpochSecond(1771000000), result.captureDate());
        assertEquals(0.64, result.ndviMean());
        assertEquals(3.2, result.cloudPercentage());
        assertTrue(result.tileUrl().startsWith("https://api.agromonitoring.com/tile/"));
        assertFalse(result.tileUrl().contains("appid"));
        server.verify();
        verify(repository, times(2)).save(any(AgroMonitoringPlotIntegrationEntity.class));
    }

    @Test
    void streamsNdviTileWithServerSideApiKey() {
        var properties = configuredProperties();
        var repository = mock(SpringDataAgroMonitoringPlotIntegrationRepository.class);
        var plot = createPlot();

        var integration = new AgroMonitoringPlotIntegrationEntity();
        integration.setPlotId(1L);
        integration.setExternalPolygonId("provider-polygon-1");
        integration.setBoundaryFingerprint(boundaryFingerprintOf(plot));
        integration.setTileUrl("https://api.agromonitoring.com/tile/1.0/{z}/{x}/{y}/latest");
        when(repository.findByPlotId(1L)).thenReturn(Optional.of(integration));

        var restClientBuilder = RestClient.builder();
        var server = MockRestServiceServer.bindTo(restClientBuilder).build();
        var adapter = new AgroMonitoringImageryServiceAdapter(
                properties,
                repository,
                restClientBuilder.baseUrl(properties.getBaseUrl()).build()
        );

        var tileBytes = new byte[]{(byte) 0x89, 'P', 'N', 'G'};
        server.expect(once(), request -> {
                    assertEquals("/tile/1.0/12/1180/2122/latest", request.getURI().getPath());
                    assertEquals("appid=test-key", request.getURI().getQuery());
                })
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(tileBytes, MediaType.IMAGE_PNG));

        var result = adapter.fetchCurrentNdviTile(plot, 12, 1180, 2122);

        assertArrayEquals(tileBytes, result.orElseThrow());
        server.verify();
    }

    @Test
    void returnsEmptyTileWhenNoImageryIsCached() {
        var properties = configuredProperties();
        var repository = mock(SpringDataAgroMonitoringPlotIntegrationRepository.class);
        when(repository.findByPlotId(1L)).thenReturn(Optional.empty());

        var adapter = new AgroMonitoringImageryServiceAdapter(
                properties,
                repository,
                RestClient.create()
        );

        assertTrue(adapter.fetchCurrentNdviTile(createPlot(), 12, 1180, 2122).isEmpty());
    }

    /**
     * Mirrors the adapter's boundary fingerprint (SHA-256 over the canonical
     * hex-encoded coordinates) so cached integrations match in tile tests.
     */
    private String boundaryFingerprintOf(Plot plot) {
        var canonicalCoordinates = new StringBuilder();
        plot.getPolygonCoordinates().getPoints().forEach(point -> canonicalCoordinates
                .append(Double.toHexString(point.getLongitude()))
                .append(',')
                .append(Double.toHexString(point.getLatitude()))
                .append(';'));
        try {
            var digest = java.security.MessageDigest.getInstance("SHA-256")
                    .digest(canonicalCoordinates.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (java.security.NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available.", exception);
        }
    }

    @Test
    void replacesProviderPolygonWhenBoundaryFingerprintChanges() {
        var properties = configuredProperties();
        var repository = mock(SpringDataAgroMonitoringPlotIntegrationRepository.class);
        var existingIntegration = new AgroMonitoringPlotIntegrationEntity();
        existingIntegration.setPlotId(1L);
        existingIntegration.setExternalPolygonId("old-provider-polygon");
        existingIntegration.setBoundaryFingerprint("outdated-boundary");
        existingIntegration.setProviderImageryId("old-image");
        existingIntegration.setTileUrl("https://old-tile");
        existingIntegration.setCaptureDate(Instant.parse("2026-01-01T00:00:00Z"));
        existingIntegration.setNdviMean(0.40);
        existingIntegration.setCloudPercentage(5.0);

        when(repository.findByPlotId(1L)).thenReturn(Optional.of(existingIntegration));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var restClientBuilder = RestClient.builder();
        var server = MockRestServiceServer.bindTo(restClientBuilder).build();
        var adapter = new AgroMonitoringImageryServiceAdapter(
                properties,
                repository,
                restClientBuilder.baseUrl(properties.getBaseUrl()).build()
        );

        server.expect(once(), request ->
                        assertEquals("/agro/1.0/polygons", request.getURI().getPath()))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(
                        "{\"id\":\"new-provider-polygon\"}",
                        MediaType.APPLICATION_JSON
                ));

        server.expect(once(), request -> assertEquals(
                        "/agro/1.0/polygons/old-provider-polygon",
                        request.getURI().getPath()
                ))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withNoContent());

        server.expect(once(), request -> {
                    assertEquals("/agro/1.0/image/search", request.getURI().getPath());
                    assertTrue(request.getURI().getQuery().contains(
                            "polyid=new-provider-polygon"
                    ));
                })
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        var result = adapter.findCurrentImagery(createPlot());

        assertTrue(result.isEmpty());
        assertEquals("new-provider-polygon", existingIntegration.getExternalPolygonId());
        assertNotEquals("outdated-boundary", existingIntegration.getBoundaryFingerprint());
        assertNull(existingIntegration.getProviderImageryId());
        assertNull(existingIntegration.getTileUrl());
        assertTrue(existingIntegration.getLastCheckedAt() != null);
        server.verify();
    }

    private AgroMonitoringProperties configuredProperties() {
        var properties = new AgroMonitoringProperties();
        properties.setEnabled(true);
        properties.setBaseUrl("https://api.agromonitoring.com");
        properties.setApiKey("test-key");
        properties.setRefreshIntervalMinutes(60);
        return properties;
    }

    private Plot createPlot() {
        var firstPoint = new GeoPoint(-12.0, -77.0);
        return new Plot(
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
        ).restoreIdentity(new PlotId(1L));
    }
}
