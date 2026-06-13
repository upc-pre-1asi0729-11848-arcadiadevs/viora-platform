package com.arcadiadevs.viora.platform.agronomic.application.queryservices;

import com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices.WeatherDataService;
import com.arcadiadevs.viora.platform.agronomic.application.readmodels.PlotWeatherForecast;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotWeatherForecastQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.WeatherForecastAdvisor;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.TimeRange;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherReading;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.PlotRepository;
import com.arcadiadevs.viora.platform.shared.application.result.ApplicationError;
import com.arcadiadevs.viora.platform.shared.application.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Query service for the per-plot weather forecast.
 *
 * <p>
 * Retrieves the provider forecast (cached at the adapter to protect the
 * rate-limited account), derives daily aggregates, thermal anomaly and
 * agronomic warnings, and reports source freshness. A missing forecast degrades
 * to an empty projection rather than failing the request.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class PlotWeatherForecastQueryService {

    private static final TimeRange BASELINE_WINDOW = TimeRange.LAST_7_DAYS;

    private final PlotRepository plotRepository;
    private final WeatherDataService weatherDataService;
    private final WeatherForecastAdvisor weatherForecastAdvisor;

    @Transactional
    public Result<PlotWeatherForecast, ApplicationError> handle(GetPlotWeatherForecastQuery query) {
        var userId = new UserId(query.userId());
        var plotId = new PlotId(query.plotId());

        var plotOptional = plotRepository.findById(plotId);
        if (plotOptional.isEmpty() || !plotOptional.get().isActive()) {
            return Result.failure(ApplicationError.notFound("plot", query.plotId().toString()));
        }

        var plot = plotOptional.get();
        if (!plot.belongsTo(userId)) {
            return Result.failure(ApplicationError.forbidden(
                    "plot-ownership",
                    "User %d does not own plot %d.".formatted(query.userId(), query.plotId())
            ));
        }

        var source = weatherDataService.describeSource(plot);
        var forecastOptional = weatherDataService.getForecast(plot);
        if (forecastOptional.isEmpty()) {
            return Result.success(new PlotWeatherForecast(
                    plot, null, List.of(), List.of(), null, null, List.of(), source));
        }

        var forecast = forecastOptional.get();
        var baselineMean = resolveBaselineMean(plot);
        var analysis = weatherForecastAdvisor.analyze(forecast, baselineMean);

        return Result.success(new PlotWeatherForecast(
                plot,
                forecast.generatedAt(),
                forecast.readings(),
                analysis.dailyForecasts(),
                analysis.thermalAnomalyCelsius(),
                analysis.overallRisk(),
                analysis.warnings(),
                source
        ));
    }

    /* Recent mean temperature used as the thermal-anomaly baseline, or null. */
    private Double resolveBaselineMean(Plot plot) {
        return weatherDataService.getWeatherHistory(plot, BASELINE_WINDOW.toDateRange(LocalDate.now()))
                .map(history -> history.readings().stream()
                        .mapToDouble(WeatherReading::temperatureCelsius)
                        .average()
                        .orElse(Double.NaN))
                .filter(Double::isFinite)
                .orElse(null);
    }
}
