package com.arcadiadevs.viora.platform.agronomic.infrastructure.external.agromonitoring;

import com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices.AgroMonitoringImageryService;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DataSourceMetadata;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DateRange;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviHistory;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviStatistic;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ProviderDataAvailability;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.SatelliteImagery;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * AgroMonitoring adapter used to register plot polygons and obtain current NDVI imagery.
 */
@Service
@Slf4j
public class AgroMonitoringImageryServiceAdapter implements AgroMonitoringImageryService {

    private static final String PROVIDER = "AgroMonitoring";
    private static final int HTTP_TOO_MANY_REQUESTS = 429;

    private final AgroMonitoringProperties properties;
    private final SpringDataAgroMonitoringPlotIntegrationRepository integrationRepository;
    private final RestClient restClient;
    private final AgroMonitoringQuotaGuard quotaGuard;
    private final ExpiringCache<NdviHistory> ndviHistoryCache;

    public AgroMonitoringImageryServiceAdapter(
            AgroMonitoringProperties properties,
            SpringDataAgroMonitoringPlotIntegrationRepository integrationRepository,
            @Qualifier("agroMonitoringRestClient") RestClient restClient,
            AgroMonitoringQuotaGuard quotaGuard
    ) {
        this.properties = properties;
        this.integrationRepository = integrationRepository;
        this.restClient = restClient;
        this.quotaGuard = quotaGuard;
        this.ndviHistoryCache = new ExpiringCache<>(
                Duration.ofMinutes(properties.getNdviHistoryCacheTtlMinutes()));
    }

    @Override
    public boolean isIntegrationEnabled() {
        return properties.isConfigured();
    }

    @Override
    public boolean isPlotLinked(Plot plot) {
        if (!properties.isConfigured() || plot == null || plot.getId() == null) {
            return false;
        }

        var expectedFingerprint = boundaryFingerprint(plot);
        return integrationRepository.findByPlotId(plot.getId().getValue())
                .filter(integration ->
                        expectedFingerprint.equals(integration.getBoundaryFingerprint())
                )
                .isPresent();
    }

    @Override
    @Transactional
    public Optional<SatelliteImagery> findCurrentImagery(Plot plot) {
        if (!properties.isConfigured()) {
            return Optional.empty();
        }

        var boundaryFingerprint = boundaryFingerprint(plot);

        if (quotaGuard.isQuotaExhausted()) {
            return cachedImageryFor(plot, boundaryFingerprint);
        }

        try {
            var integration = findOrRegisterPlot(plot, boundaryFingerprint);

            if (wasCheckedRecently(integration)) {
                return toCachedImagery(integration);
            }

            var currentImage = searchCurrentImage(integration.getExternalPolygonId());
            integration.setLastCheckedAt(Instant.now());

            if (currentImage.isEmpty()) {
                integrationRepository.save(integration);
                return toCachedImagery(integration);
            }

            var image = currentImage.get();
            var ndviMean = fetchNdviMean(image.stats().ndvi()).orElse(null);

            integration.setProviderImageryId(Long.toString(image.dt()));
            integration.setTileUrl(withoutApiKey(image.tile().ndvi()));
            integration.setCaptureDate(Instant.ofEpochSecond(image.dt()));
            integration.setNdviMean(ndviMean);
            integration.setCloudPercentage(image.cl());
            integrationRepository.save(integration);

            return toCachedImagery(integration);
        } catch (RestClientException | IllegalArgumentException exception) {
            handleProviderFailure("imagery", plot, exception);
            return cachedImageryFor(plot, boundaryFingerprint);
        }
    }

    private Optional<SatelliteImagery> cachedImageryFor(Plot plot, String boundaryFingerprint) {
        return integrationRepository.findByPlotId(plot.getId().getValue())
                .filter(integration ->
                        boundaryFingerprint.equals(integration.getBoundaryFingerprint())
                )
                .flatMap(this::toCachedImagery);
    }

    private AgroMonitoringPlotIntegrationEntity findOrRegisterPlot(
            Plot plot,
            String boundaryFingerprint
    ) {
        return integrationRepository.findByPlotId(plot.getId().getValue())
                .map(integration -> synchronizeBoundary(
                        integration,
                        plot,
                        boundaryFingerprint
                ))
                .orElseGet(() -> registerPlot(plot, boundaryFingerprint));
    }

    private AgroMonitoringPlotIntegrationEntity registerPlot(
            Plot plot,
            String boundaryFingerprint
    ) {
        var response = createProviderPolygon(plot);
        var integration = new AgroMonitoringPlotIntegrationEntity();
        integration.setPlotId(plot.getId().getValue());
        integration.setExternalPolygonId(response.id());
        integration.setBoundaryFingerprint(boundaryFingerprint);
        return integrationRepository.save(integration);
    }

    private AgroMonitoringPlotIntegrationEntity synchronizeBoundary(
            AgroMonitoringPlotIntegrationEntity integration,
            Plot plot,
            String boundaryFingerprint
    ) {
        if (boundaryFingerprint.equals(integration.getBoundaryFingerprint())) {
            return integration;
        }

        var oldExternalPolygonId = integration.getExternalPolygonId();
        var response = createProviderPolygon(plot);

        integration.setExternalPolygonId(response.id());
        integration.setBoundaryFingerprint(boundaryFingerprint);
        integration.setProviderImageryId(null);
        integration.setTileUrl(null);
        integration.setCaptureDate(null);
        integration.setNdviMean(null);
        integration.setCloudPercentage(null);
        integration.setLastCheckedAt(null);

        var synchronizedIntegration = integrationRepository.save(integration);
        removeProviderPolygon(oldExternalPolygonId);
        return synchronizedIntegration;
    }

    private AgroMonitoringPolygonResponse createProviderPolygon(Plot plot) {
        var response = restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/agro/1.0/polygons")
                        .queryParam("appid", properties.getApiKey())
                        .queryParam("duplicated", true)
                        .build())
                .body(toCreatePolygonRequest(plot))
                .retrieve()
                .body(AgroMonitoringPolygonResponse.class);

        if (response == null || response.id() == null || response.id().isBlank()) {
            throw new IllegalArgumentException("AgroMonitoring did not return a polygon ID.");
        }

        return response;
    }

    private void removeProviderPolygon(String externalPolygonId) {
        try {
            restClient.delete()
                    .uri(uriBuilder -> uriBuilder
                            .path("/agro/1.0/polygons/{polygonId}")
                            .queryParam("appid", properties.getApiKey())
                            .build(externalPolygonId))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException exception) {
            log.warn(
                    "Unable to remove obsolete AgroMonitoring polygon {} ({}).",
                    externalPolygonId,
                    providerFailureReason(exception)
            );
        }
    }

    private Optional<AgroMonitoringImageResponse> searchCurrentImage(String externalPolygonId) {
        var end = Instant.now();
        var start = end.minus(properties.getImageryLookbackDays(), ChronoUnit.DAYS);

        var response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/agro/1.0/image/search")
                        .queryParam("start", start.getEpochSecond())
                        .queryParam("end", end.getEpochSecond())
                        .queryParam("polyid", externalPolygonId)
                        .queryParam("clouds_max", properties.getMaximumCloudPercentage())
                        .queryParam("appid", properties.getApiKey())
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<AgroMonitoringImageResponse>>() {
                });

        if (response == null) {
            return Optional.empty();
        }

        return response.stream()
                .filter(image -> image.tile() != null)
                .filter(image -> image.stats() != null)
                .filter(image -> image.tile().ndvi() != null && !image.tile().ndvi().isBlank())
                .filter(image -> image.stats().ndvi() != null && !image.stats().ndvi().isBlank())
                .max(Comparator.comparingLong(AgroMonitoringImageResponse::dt));
    }

    private Optional<Double> fetchNdviMean(String statisticsUrl) {
        try {
            var response = restClient.get()
                    .uri(withApiKey(statisticsUrl))
                    .retrieve()
                    .body(AgroMonitoringStatisticsResponse.class);
            return response == null ? Optional.empty() : Optional.ofNullable(response.mean());
        } catch (RestClientException exception) {
            log.warn(
                    "Unable to obtain AgroMonitoring NDVI statistics ({}).",
                    providerFailureReason(exception)
            );
            return Optional.empty();
        }
    }

    @Override
    public Optional<byte[]> fetchCurrentNdviTile(Plot plot, int zoom, int x, int y) {
        if (!properties.isConfigured()) {
            return Optional.empty();
        }

        var tileUrlTemplate = integrationRepository.findByPlotId(plot.getId().getValue())
                .filter(integration -> boundaryFingerprint(plot).equals(integration.getBoundaryFingerprint()))
                .map(AgroMonitoringPlotIntegrationEntity::getTileUrl)
                .orElse(null);

        if (tileUrlTemplate == null) {
            return Optional.empty();
        }

        var tileUrl = withApiKey(tileUrlTemplate
                .replace("{z}", Integer.toString(zoom))
                .replace("{x}", Integer.toString(x))
                .replace("{y}", Integer.toString(y)));

        try {
            var tileBytes = restClient.get()
                    .uri(tileUrl)
                    .retrieve()
                    .body(byte[].class);
            return Optional.ofNullable(tileBytes).filter(bytes -> bytes.length > 0);
        } catch (RestClientException exception) {
            if (isQuotaRejection(exception)) {
                quotaGuard.recordQuotaExceeded();
            }
            log.warn(
                    "Unable to fetch AgroMonitoring NDVI tile {}/{}/{} for plot {} ({}).",
                    zoom,
                    x,
                    y,
                    plot.getId().getValue(),
                    providerFailureReason(exception)
            );
            return Optional.empty();
        }
    }

    @Override
    public Optional<NdviHistory> findNdviHistory(Plot plot, DateRange range) {
        if (range == null) {
            throw new IllegalArgumentException("NDVI history range is required.");
        }
        if (!properties.isConfigured() || quotaGuard.isQuotaExhausted()) {
            return Optional.empty();
        }

        var externalPolygonId = integrationRepository.findByPlotId(plot.getId().getValue())
                .filter(integration -> boundaryFingerprint(plot).equals(integration.getBoundaryFingerprint()))
                .map(AgroMonitoringPlotIntegrationEntity::getExternalPolygonId)
                .orElse(null);

        if (externalPolygonId == null) {
            return Optional.empty();
        }

        var cacheKey = externalPolygonId + ":" + range.getStartDate() + ":" + range.getEndDate();
        var cached = ndviHistoryCache.get(cacheKey);
        if (cached.isPresent()) {
            return cached;
        }

        try {
            var response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/agro/1.0/ndvi/history")
                            .queryParam("polyid", externalPolygonId)
                            .queryParam("start", startEpochSecond(range))
                            .queryParam("end", endEpochSecond(range))
                            .queryParam("appid", properties.getApiKey())
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<AgroMonitoringNdviHistoryEntry>>() {
                    });

            var statistics = toNdviStatistics(response);
            if (statistics.isEmpty()) {
                return Optional.empty();
            }

            var history = new NdviHistory(statistics);
            ndviHistoryCache.put(cacheKey, history);
            return Optional.of(history);
        } catch (RestClientException | IllegalArgumentException exception) {
            handleProviderFailure("NDVI history", plot, exception);
            return Optional.empty();
        }
    }

    @Override
    public DataSourceMetadata describeNdviSource(Plot plot) {
        if (!properties.isConfigured()) {
            return DataSourceMetadata.notConfigured(PROVIDER);
        }

        var integration = integrationRepository.findByPlotId(plot.getId().getValue())
                .filter(linked -> boundaryFingerprint(plot).equals(linked.getBoundaryFingerprint()))
                .orElse(null);

        if (integration == null) {
            return new DataSourceMetadata(PROVIDER, ProviderDataAvailability.NOT_LINKED, null, null);
        }

        var availability = quotaGuard.isQuotaExhausted()
                ? ProviderDataAvailability.QUOTA_EXCEEDED
                : ProviderDataAvailability.AVAILABLE;

        return new DataSourceMetadata(
                PROVIDER,
                availability,
                integration.getCaptureDate(),
                properties.getRefreshIntervalMinutes()
        );
    }

    private List<NdviStatistic> toNdviStatistics(List<AgroMonitoringNdviHistoryEntry> entries) {
        if (entries == null) {
            return List.of();
        }
        return entries.stream()
                .map(this::toNdviStatistic)
                .flatMap(Optional::stream)
                .toList();
    }

    private Optional<NdviStatistic> toNdviStatistic(AgroMonitoringNdviHistoryEntry entry) {
        if (entry == null || entry.data() == null || entry.data().mean() == null) {
            return Optional.empty();
        }

        var data = entry.data();
        try {
            return Optional.of(new NdviStatistic(
                    Instant.ofEpochSecond(entry.dt()),
                    data.mean(),
                    data.min(),
                    data.max(),
                    data.median(),
                    data.std(),
                    data.p25(),
                    data.p75()
            ));
        } catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }

    private long startEpochSecond(DateRange range) {
        return range.getStartDate().atStartOfDay(ZoneOffset.UTC).toEpochSecond();
    }

    private long endEpochSecond(DateRange range) {
        return range.getEndDate().plusDays(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond();
    }

    private void handleProviderFailure(String operation, Plot plot, Exception exception) {
        if (isQuotaRejection(exception)) {
            quotaGuard.recordQuotaExceeded();
        }
        log.warn(
                "Unable to refresh AgroMonitoring {} for plot {} ({}).",
                operation,
                plot.getId().getValue(),
                providerFailureReason(exception)
        );
    }

    private boolean isQuotaRejection(Exception exception) {
        return exception instanceof RestClientResponseException responseException
                && responseException.getStatusCode().value() == HTTP_TOO_MANY_REQUESTS;
    }

    /* The cached tile URL is exposed without provider credentials; clients consume
       tiles through the platform proxy endpoint instead of calling the provider. */
    private Optional<SatelliteImagery> toCachedImagery(
            AgroMonitoringPlotIntegrationEntity integration
    ) {
        if (integration.getProviderImageryId() == null
                || integration.getTileUrl() == null
                || integration.getCaptureDate() == null
                || integration.getCloudPercentage() == null) {
            return Optional.empty();
        }

        return Optional.of(new SatelliteImagery(
                integration.getProviderImageryId(),
                integration.getTileUrl(),
                integration.getCaptureDate(),
                integration.getNdviMean(),
                integration.getCloudPercentage()
        ));
    }

    private boolean wasCheckedRecently(AgroMonitoringPlotIntegrationEntity integration) {
        return properties.getRefreshIntervalMinutes() > 0
                && integration.getLastCheckedAt() != null
                && integration.getLastCheckedAt().isAfter(
                        Instant.now().minus(
                                properties.getRefreshIntervalMinutes(),
                                ChronoUnit.MINUTES
                        )
                );
    }

    private AgroMonitoringCreatePolygonRequest toCreatePolygonRequest(Plot plot) {
        var coordinates = plot.getPolygonCoordinates().getPoints().stream()
                .map(point -> List.of(point.getLongitude(), point.getLatitude()))
                .toList();

        var geometry = new AgroMonitoringGeometry("Polygon", List.of(coordinates));
        var feature = new AgroMonitoringGeoJson("Feature", Map.of(), geometry);
        return new AgroMonitoringCreatePolygonRequest(plot.getName().getValue(), feature);
    }

    private String withoutApiKey(String url) {
        var normalized = url.startsWith("http://")
                ? "https://" + url.substring("http://".length())
                : url;
        var queryIndex = normalized.indexOf('?');
        return queryIndex >= 0 ? normalized.substring(0, queryIndex) : normalized;
    }

    private String withApiKey(String url) {
        return withoutApiKey(url)
                + "?appid="
                + URLEncoder.encode(properties.getApiKey(), StandardCharsets.UTF_8);
    }

    private String boundaryFingerprint(Plot plot) {
        var canonicalCoordinates = new StringBuilder();
        plot.getPolygonCoordinates().getPoints().forEach(point -> canonicalCoordinates
                .append(Double.toHexString(point.getLongitude()))
                .append(',')
                .append(Double.toHexString(point.getLatitude()))
                .append(';'));

        try {
            var digest = MessageDigest.getInstance("SHA-256")
                    .digest(canonicalCoordinates.toString().getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available.", exception);
        }
    }

    private String providerFailureReason(Exception exception) {
        if (exception instanceof RestClientResponseException responseException) {
            return "HTTP " + responseException.getStatusCode().value();
        }
        return exception.getClass().getSimpleName();
    }

    private record AgroMonitoringCreatePolygonRequest(
            String name,
            @JsonProperty("geo_json") AgroMonitoringGeoJson geoJson
    ) {
    }

    private record AgroMonitoringGeoJson(
            String type,
            Map<String, Object> properties,
            AgroMonitoringGeometry geometry
    ) {
    }

    private record AgroMonitoringGeometry(
            String type,
            List<List<List<Double>>> coordinates
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record AgroMonitoringPolygonResponse(String id) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record AgroMonitoringImageResponse(
            long dt,
            double cl,
            AgroMonitoringProductUrls tile,
            AgroMonitoringProductUrls stats
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record AgroMonitoringProductUrls(String ndvi) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record AgroMonitoringStatisticsResponse(Double mean) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record AgroMonitoringNdviHistoryEntry(
            long dt,
            AgroMonitoringNdviStatistics data
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record AgroMonitoringNdviStatistics(
            Double mean,
            Double min,
            Double max,
            Double median,
            Double std,
            Double p25,
            Double p75
    ) {
    }
}
