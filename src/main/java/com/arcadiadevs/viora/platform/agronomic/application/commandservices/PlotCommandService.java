package com.arcadiadevs.viora.platform.agronomic.application.commandservices;

import com.arcadiadevs.viora.platform.agronomic.domain.exceptions.InvalidPolygonCoordinatesException;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.CreatePlotCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.DeletePlotCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.UpdatePlotCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.PlotDeletionPolicy;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AreaSize;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotName;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PolygonCoordinates;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.PlotRepository;
import com.arcadiadevs.viora.platform.shared.application.result.ApplicationError;
import com.arcadiadevs.viora.platform.shared.application.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Plot command service.
 *
 * <p>
 * Application service responsible for handling write operations related to plots.
 * It uses Result and ApplicationError from shared to return explicit success or failure responses.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class PlotCommandService {

    /**
     * Plot repository port.
     */
    private final PlotRepository plotRepository;

    /**
     * Plot deletion policy.
     */
    private final PlotDeletionPolicy plotDeletionPolicy = new PlotDeletionPolicy();

    /**
     * Handles plot registration.
     *
     * @param command The command containing the new plot data.
     * @return A successful result with the persisted plot, or an application error.
     */
    @Transactional
    public Result<Plot, ApplicationError> handle(CreatePlotCommand command) {
        try {
            var userId = new UserId(command.userId());
            var plotName = new PlotName(command.name());

            if (plotRepository.existsByNameAndUserId(plotName, userId)) {
                return Result.failure(ApplicationError.conflict(
                        "plot",
                        "A plot with the same name already exists for this user."
                ));
            }

            var plot = new Plot(
                    userId,
                    plotName,
                    toPolygonCoordinates(command.polygonCoordinates()),
                    new AreaSize(command.areaSizeHectares()),
                    command.cropType(),
                    command.variety()
            );

            return Result.success(plotRepository.save(plot));
        } catch (IllegalArgumentException | InvalidPolygonCoordinatesException exception) {
            return Result.failure(ApplicationError.validationError(
                    "plot",
                    exception.getMessage()
            ));
        }
    }

    /**
     * Handles the UpdatePlot command.
     *
     * @param command The command containing the update data.
     * @return A successful result with the updated plot, or an application error.
     */
    @Transactional
    public Result<Plot, ApplicationError> handle(UpdatePlotCommand command) {
        var plotId = new PlotId(command.plotId());
        var plotOptional = plotRepository.findById(plotId);

        if (plotOptional.isEmpty()) {
            return Result.failure(ApplicationError.notFound("plot", command.plotId().toString()));
        }

        var plot = plotOptional.get();

        if (!plot.isActive()) {
            return Result.failure(ApplicationError.businessRuleViolation(
                    "update-active-plot",
                    "Only active plots can be updated."
            ));
        }

        try {
            var updatedName = command.name() != null
                    ? new PlotName(command.name())
                    : plot.getName();

            var updatedAreaSize = command.areaSizeHectares() != null
                    ? new AreaSize(command.areaSizeHectares())
                    : plot.getAreaSize();

            var updatedCropType = command.cropType() != null
                    ? command.cropType()
                    : plot.getCropType();

            var updatedVariety = command.variety() != null
                    ? command.variety()
                    : plot.getVariety();

            var updatedPolygonCoordinates = command.polygonCoordinates() != null
                    ? toPolygonCoordinates(command.polygonCoordinates())
                    : plot.getPolygonCoordinates();

            if (hasDuplicatedName(command, plot, updatedName)) {
                return Result.failure(ApplicationError.conflict(
                        "plot",
                        "A plot with the same name already exists for this user."
                ));
            }

            plot.updateInformation(
                    updatedName,
                    updatedAreaSize,
                    updatedCropType,
                    updatedVariety
            );

            plot.updateBoundary(updatedPolygonCoordinates, updatedAreaSize);

            var updatedPlot = plotRepository.save(plot);
            return Result.success(updatedPlot);

        } catch (IllegalArgumentException | InvalidPolygonCoordinatesException exception) {
            return Result.failure(ApplicationError.validationError(
                    "plot",
                    exception.getMessage()
            ));
        }
    }

    /**
     * Checks if the requested name already belongs to another plot of the same user.
     *
     * @param command The update command.
     * @param plot The existing plot.
     * @return true if another plot with the same name exists, false otherwise.
     */
    private boolean hasDuplicatedName(UpdatePlotCommand command, Plot plot, PlotName updatedName) {
        if (command.name() == null) {
            return false;
        }

        return plotRepository.existsByNameAndUserIdAndIdIsNot(
                updatedName,
                plot.getUserId(),
                plot.getId()
        );
    }

    /**
     * Converts a frontend coordinate list into PolygonCoordinates.
     *
     * @param rawCoordinates The frontend coordinate list.
     * @return The domain polygon coordinates.
     */
    private PolygonCoordinates toPolygonCoordinates(List<List<Double>> rawCoordinates) {
        if (rawCoordinates == null || rawCoordinates.isEmpty()) {
            throw new IllegalArgumentException("Polygon coordinates are required.");
        }

        var points = rawCoordinates.stream()
                .map(this::toGeoPoint)
                .toList();

        return new PolygonCoordinates(points);
    }

    /**
     * Converts a raw coordinate pair into a GeoPoint.
     *
     * @param rawPoint The raw coordinate pair.
     * @return The GeoPoint value object.
     */
    private GeoPoint toGeoPoint(List<Double> rawPoint) {
        if (rawPoint == null || rawPoint.size() != 2) {
            throw new IllegalArgumentException("Each polygon coordinate must contain latitude and longitude.");
        }

        return new GeoPoint(rawPoint.get(0), rawPoint.get(1));
    }

    /**
     * Handles the DeletePlot command.
     *
     * @param command The command containing the plot identifier.
     * @return A successful result with a confirmation message, or an application error.
     */
    @Transactional
    public Result<String, ApplicationError> handle(DeletePlotCommand command) {
        var plotId = new PlotId(command.plotId());
        var plotOptional = plotRepository.findById(plotId);

        if (plotOptional.isEmpty()) {
            return Result.failure(ApplicationError.notFound("plot", command.plotId().toString()));
        }

        var plot = plotOptional.get();

        if (!plotDeletionPolicy.canDelete(plot)) {
            return Result.failure(ApplicationError.businessRuleViolation(
                    "delete-active-plot",
                    plotDeletionPolicy.explainDeletionRejection(plot)
            ));
        }

        var hasRelatedOperationalRecords = plotRepository.hasRelatedOperationalRecords(plotId);

        if (plotDeletionPolicy.requiresLogicalDeletion(hasRelatedOperationalRecords)) {
            plot.deactivate();
            plotRepository.save(plot);
        } else {
            plotRepository.deleteById(plotId);
        }

        return Result.success("Plot deleted successfully.");
    }

}
