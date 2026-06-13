package com.arcadiadevs.viora.platform.agronomic.application.commandservices;

import com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices.AgroMonitoringImageryService;
import com.arcadiadevs.viora.platform.agronomic.application.readmodels.IntegrationLinkStatus;
import com.arcadiadevs.viora.platform.agronomic.application.readmodels.PlotRegistration;
import com.arcadiadevs.viora.platform.agronomic.domain.exceptions.InvalidPolygonCoordinatesException;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.ConfigureChillRequirementCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.CreatePlotCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.DeletePlotCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.ResetChillRequirementCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.UpdatePlotCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.ChillRequirementResolver;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.PlotDeletionPolicy;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillPortions;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillRequirement;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillRequirementSource;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotName;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PolygonCoordinates;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.SatelliteImagery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.PlotRepository;
import com.arcadiadevs.viora.platform.shared.application.result.ApplicationError;
import com.arcadiadevs.viora.platform.shared.application.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
     * Satellite and climate provider integration.
     */
    private final AgroMonitoringImageryService agroMonitoringImageryService;

    /**
     * Resolves the effective chill requirement after a configuration change.
     */
    private final ChillRequirementResolver chillRequirementResolver;

    /**
     * Plot deletion policy.
     */
    private final PlotDeletionPolicy plotDeletionPolicy = new PlotDeletionPolicy();

    /**
     * Handles plot registration.
     *
     * @param command The command containing the new plot data.
     * @return A successful result with the persisted plot and initial integration
     *         states, or an application error.
     */
    @Transactional
    public Result<PlotRegistration, ApplicationError> handle(CreatePlotCommand command) {
        try {
            var userId = new UserId(command.userId());
            var plotName = new PlotName(command.name());

            if (plotRepository.existsByNameAndUserId(plotName, userId)) {
                return Result.failure(ApplicationError.conflict(
                        "plot",
                        "A plot with the same name already exists for this user."
                ));
            }

            var polygonCoordinates = toPolygonCoordinates(command.polygonCoordinates());
            var plot = Plot.register(
                    userId,
                    plotName,
                    polygonCoordinates,
                    command.cropType(),
                    command.variety(),
                    command.location(),
                    command.campaign(),
                    command.notes()
            );

            var savedPlot = plotRepository.save(plot);
            var imagery = agroMonitoringImageryService.isIntegrationEnabled()
                    ? agroMonitoringImageryService.findCurrentImagery(savedPlot)
                    : Optional.<SatelliteImagery>empty();

            var linkedToProvider = agroMonitoringImageryService.isPlotLinked(savedPlot);
            var climateMonitoring = linkedToProvider
                    ? IntegrationLinkStatus.ACTIVE
                    : IntegrationLinkStatus.NOT_LINKED;
            var satelliteNdvi = imagery.isPresent()
                    ? IntegrationLinkStatus.ACTIVE
                    : linkedToProvider
                    ? IntegrationLinkStatus.INITIALIZING
                    : IntegrationLinkStatus.NOT_LINKED;

            return Result.success(new PlotRegistration(
                    savedPlot,
                    climateMonitoring,
                    satelliteNdvi,
                    IntegrationLinkStatus.NOT_LINKED
            ));
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

            var updatedCropType = command.cropType() != null
                    ? command.cropType()
                    : plot.getCropType();

            var updatedVariety = command.variety() != null
                    ? command.variety()
                    : plot.getVariety();

            var updatedLocation = command.location() != null
                    ? command.location()
                    : plot.getLocation();

            var updatedCampaign = command.campaign() != null
                    ? command.campaign()
                    : plot.getCampaign();

            var updatedNotes = command.notes() != null
                    ? command.notes()
                    : plot.getNotes();

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
                    updatedCropType,
                    updatedVariety,
                    updatedLocation,
                    updatedCampaign,
                    updatedNotes
            );

            if (command.polygonCoordinates() != null) {
                plot.updateBoundary(updatedPolygonCoordinates);
            }

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
     * Handles the ConfigureChillRequirement command, storing a grower-declared
     * chill requirement that overrides the crop-derived system default.
     *
     * @param command The command carrying the plot, owner and declared requirement.
     * @return The effective chill requirement after the change, or an application error.
     */
    @Transactional
    public Result<ChillRequirement, ApplicationError> handle(ConfigureChillRequirementCommand command) {
        var plotId = new PlotId(command.plotId());
        var plotOptional = plotRepository.findById(plotId);

        if (plotOptional.isEmpty() || !plotOptional.get().isActive()) {
            return Result.failure(ApplicationError.notFound("plot", command.plotId().toString()));
        }

        var plot = plotOptional.get();
        if (!plot.belongsTo(new UserId(command.userId()))) {
            return Result.failure(ApplicationError.forbidden(
                    "plot-ownership",
                    "User %d does not own plot %d.".formatted(command.userId(), command.plotId())
            ));
        }

        try {
            plot.configureChillRequirement(
                    new ChillPortions(command.chillRequirementPortions()),
                    ChillRequirementSource.USER_DECLARED
            );
            var savedPlot = plotRepository.save(plot);
            return Result.success(chillRequirementResolver.resolveFor(savedPlot));
        } catch (IllegalArgumentException exception) {
            return Result.failure(ApplicationError.validationError(
                    "chill-requirement",
                    exception.getMessage()
            ));
        }
    }

    /**
     * Handles the ResetChillRequirement command, clearing any declared override so
     * the crop-derived system default applies again.
     *
     * @param command The command carrying the plot and owner.
     * @return The effective (system-default) chill requirement, or an application error.
     */
    @Transactional
    public Result<ChillRequirement, ApplicationError> handle(ResetChillRequirementCommand command) {
        var plotId = new PlotId(command.plotId());
        var plotOptional = plotRepository.findById(plotId);

        if (plotOptional.isEmpty() || !plotOptional.get().isActive()) {
            return Result.failure(ApplicationError.notFound("plot", command.plotId().toString()));
        }

        var plot = plotOptional.get();
        if (!plot.belongsTo(new UserId(command.userId()))) {
            return Result.failure(ApplicationError.forbidden(
                    "plot-ownership",
                    "User %d does not own plot %d.".formatted(command.userId(), command.plotId())
            ));
        }

        plot.clearChillRequirement();
        var savedPlot = plotRepository.save(plot);
        return Result.success(chillRequirementResolver.resolveFor(savedPlot));
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
            throw new IllegalArgumentException(
                    "Each polygon coordinate must use GeoJSON order: longitude and latitude."
            );
        }

        return new GeoPoint(rawPoint.get(1), rawPoint.get(0));
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
