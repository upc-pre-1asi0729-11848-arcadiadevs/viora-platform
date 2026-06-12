package com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AreaSize;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillMetricModel;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillPortions;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillRequirement;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillRequirementSource;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotName;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PolygonCoordinates;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

/**
 * Plot aggregate root.
 *
 * <p>
 *     Represents a productive agricultural plot owned by a grower.
 *     A plot is delimited by geographic coordinates and is the base asset
 *     for satellite monitoring, weather analysis and agronomic prediction in Viora.
 * </p>
 */
@Getter
public class Plot extends AbstractDomainAggregateRoot<Plot> {
    private static final int CROP_TYPE_MAX_LENGTH = 60;
    private static final int VARIETY_MAX_LENGTH = 80;
    private static final int LOCATION_MAX_LENGTH = 120;
    private static final int CAMPAIGN_MAX_LENGTH = 60;
    private static final int NOTES_MAX_LENGTH = 500;

    /**
     * Defensive upper bound for a declared chill requirement. Dynamic Model
     * seasonal accumulation rarely exceeds ~150 chill portions even in cold
     * climates, so a higher value almost certainly indicates a data-entry error.
     */
    private static final double MAX_CHILL_REQUIREMENT_PORTIONS = 200.0;

    /**
     * The unique identifier for the plot.
     */
    private PlotId id;

    /**
     * The owner user identifier.
     */
    private UserId userId;

    /**
     * The business name assigned to the plot.
     */
    private PlotName name;

    /**
     * The geographic polygon that delimits the plot.
     */
    private PolygonCoordinates polygonCoordinates;

    /**
     * The productive area size in hectares.
     */
    private AreaSize areaSize;

    /**
     * The crop type associated with the plot.
     */
    private String cropType;

    /**
     * The crop variety associated with the plot.
     */
    private String variety;

    /**
     * The human-readable location of the plot (e.g. "Tacna, Peru").
     */
    private String location;

    /**
     * The production campaign the plot is enrolled in (e.g. "2026 campaign").
     */
    private String campaign;

    /**
     * Free-form grower notes about the plot.
     */
    private String notes;

    /**
     * Indicates whether the plot is active.
     */
    private Boolean active;

    /**
     * Grower- or agronomist-declared winter-chill requirement for this plot.
     * Null when no override has been configured and the crop-derived system
     * default applies instead.
     */
    private ChillRequirement chillRequirementOverride;

    /**
     * Default constructor for the Plot.
     */
    protected Plot() {
        this.cropType = "";
        this.variety = "";
        this.location = "";
        this.campaign = "";
        this.notes = "";
        this.active = true;
    }


    /**
     * Constructor for Plot without descriptive metadata.
     * @param userId The owner user identifies.
     * @param name The plot name.
     * @param polygonCoordinates The plot polygon coordinates.
     * @param areaSize The plot area size.
     * @param cropType The crop type.
     * @param variety The crop variety
     */
    public Plot(
            UserId userId,
            PlotName name,
            PolygonCoordinates polygonCoordinates,
            AreaSize areaSize,
            String cropType,
            String variety
    ) {
        this(userId, name, polygonCoordinates, areaSize, cropType, variety, null, null, null);
    }

    /**
     * Constructor for Plot.
     * @param userId The owner user identifies.
     * @param name The plot name.
     * @param polygonCoordinates The plot polygon coordinates.
     * @param areaSize The plot area size.
     * @param cropType The crop type.
     * @param variety The crop variety.
     * @param location The human-readable plot location.
     * @param campaign The production campaign.
     * @param notes Free-form grower notes.
     */
    public Plot(
            UserId userId,
            PlotName name,
            PolygonCoordinates polygonCoordinates,
            AreaSize areaSize,
            String cropType,
            String variety,
            String location,
            String campaign,
            String notes
    ) {
        validateRequiredFields(userId, name, polygonCoordinates, areaSize);
        this.userId = userId;
        this.name = name;
        this.polygonCoordinates = polygonCoordinates;
        this.areaSize = areaSize;
        this.cropType = sanitizeText(cropType, CROP_TYPE_MAX_LENGTH, "Crop type");
        this.variety = sanitizeText(variety, VARIETY_MAX_LENGTH, "Variety");
        this.location = sanitizeText(location, LOCATION_MAX_LENGTH, "Location");
        this.campaign = sanitizeText(campaign, CAMPAIGN_MAX_LENGTH, "Campaign");
        this.notes = sanitizeText(notes, NOTES_MAX_LENGTH, "Notes");
        this.active = true;
    }

    /**
     * Registers a plot whose area is derived from its geographic boundary.
     *
     * @param userId The owner user identifier.
     * @param name The plot name.
     * @param polygonCoordinates The plot boundary.
     * @param cropType The crop type.
     * @param variety The crop variety.
     * @param location The human-readable plot location.
     * @param campaign The production campaign.
     * @param notes Free-form grower notes.
     * @return A new active plot.
     */
    public static Plot register(
            UserId userId,
            PlotName name,
            PolygonCoordinates polygonCoordinates,
            String cropType,
            String variety,
            String location,
            String campaign,
            String notes
    ) {
        return new Plot(
                userId,
                name,
                polygonCoordinates,
                AreaSize.calculatedFrom(polygonCoordinates),
                cropType,
                variety,
                location,
                campaign,
                notes
        );
    }

    /**
     * Updates the general information of the plot.
     * @param name The new plot name.
     * @param cropType The new crop type.
     * @param variety The new crop variety.
     * @return The update plot.
     */
    public Plot updateInformation(
            PlotName name,
            String cropType,
            String variety
    ) {
        return updateInformation(name, cropType, variety, this.location, this.campaign, this.notes);
    }

    /**
     * Updates the general information of the plot, including descriptive metadata.
     * @param name The new plot name.
     * @param cropType The new crop type.
     * @param variety The new crop variety.
     * @param location The new plot location.
     * @param campaign The new production campaign.
     * @param notes The new grower notes.
     * @return The updated plot.
     */
    public Plot updateInformation(
            PlotName name,
            String cropType,
            String variety,
            String location,
            String campaign,
            String notes
    ) {
        if (name == null) {
            throw new IllegalArgumentException("Plot name is required.");
        }

        this.name = name;
        this.cropType = sanitizeText(cropType, CROP_TYPE_MAX_LENGTH, "Crop type");
        this.variety = sanitizeText(variety, VARIETY_MAX_LENGTH, "Variety");
        this.location = sanitizeText(location, LOCATION_MAX_LENGTH, "Location");
        this.campaign = sanitizeText(campaign, CAMPAIGN_MAX_LENGTH, "Campaign");
        this.notes = sanitizeText(notes, NOTES_MAX_LENGTH, "Notes");
        return this;
    }

    /**
     * Updates the geographic boundary and recalculates its associated area.
     *
     * @param polygonCoordinates The new polygon coordinates.
     * @return The updated plot.
     */
    public Plot updateBoundary(PolygonCoordinates polygonCoordinates) {
        if (polygonCoordinates == null) {
            throw new IllegalArgumentException("Polygon coordinates are required.");
        }

        this.polygonCoordinates = polygonCoordinates;
        this.areaSize = AreaSize.calculatedFrom(polygonCoordinates);
        return this;
    }

    /**
     * Deactivates the plot.
     *
     * <p>
     *     This method is useful when the system needs logical deletion
     *     instead of physical deletion.
     * </p>
     *
     * @return The deactivated plot.
     */
    public Plot deactivate() {
        this.active = false;
        return this;
    }

    /**
     * Declares an explicit winter-chill requirement for this plot, overriding the
     * crop-derived system default.
     *
     * @param portions The declared requirement, in Dynamic Model chill portions.
     * @param source The provenance of the declaration ({@code USER_DECLARED} or
     *               {@code AGRONOMIST_VALIDATED}).
     * @return The updated plot.
     */
    public Plot configureChillRequirement(ChillPortions portions, ChillRequirementSource source) {
        if (portions == null) {
            throw new IllegalArgumentException("Chill requirement portions are required.");
        }
        if (source != ChillRequirementSource.USER_DECLARED
                && source != ChillRequirementSource.AGRONOMIST_VALIDATED) {
            throw new IllegalArgumentException(
                    "A configured chill requirement must be user-declared or agronomist-validated."
            );
        }
        if (!(portions.getValue() > 0)) {
            throw new IllegalArgumentException("Chill requirement must be greater than zero.");
        }
        if (portions.getValue() > MAX_CHILL_REQUIREMENT_PORTIONS) {
            throw new IllegalArgumentException(
                    "Chill requirement cannot exceed %.0f chill portions.".formatted(MAX_CHILL_REQUIREMENT_PORTIONS)
            );
        }

        this.chillRequirementOverride = new ChillRequirement(portions, source, ChillMetricModel.DYNAMIC);
        return this;
    }

    /**
     * Clears any declared chill requirement, reverting to the crop-derived system
     * default.
     *
     * @return The updated plot.
     */
    public Plot clearChillRequirement() {
        this.chillRequirementOverride = null;
        return this;
    }

    /**
     * Checks whether the plot belongs to a specific user.
     * @param userId The user identifier.
     * @return true if the plot belongs to the user, false otherwise.
     */
    public boolean belongsTo(UserId userId) {
        return this.userId != null && this.userId.equals(userId);
    }

    /**
     * Checks whether the plot is active.
     * @return true if the plot is active, false otherwise.
     */
    public boolean isActive() {
        return Boolean.TRUE.equals(this.active);
    }

    /**
     * Restores the identity assigned by persistence.
     *
     * @param id The persisted plot identifier.
     * @return The identified plot.
     */
    public Plot restoreIdentity(PlotId id) {
        if (id == null) {
            throw new IllegalArgumentException("Plot ID is required.");
        }
        if (this.id != null && !this.id.equals(id)) {
            throw new IllegalStateException("Plot identity cannot be changed.");
        }
        this.id = id;
        return this;
    }

    /**
     * Sanitizes optional text fields.
     * @param value The text value.
     * @return The sanitized value.
     */
    private String sanitizeText(String value, int maxLength, String fieldName) {
        var sanitizedValue = value == null ? "" : value.trim();
        if (sanitizedValue.length() > maxLength) {
            throw new IllegalArgumentException(
                    "%s cannot exceed %d characters.".formatted(fieldName, maxLength)
            );
        }
        return sanitizedValue;
    }

    private void validateRequiredFields(
            UserId userId,
            PlotName name,
            PolygonCoordinates polygonCoordinates,
            AreaSize areaSize
    ) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required.");
        }
        if (name == null) {
            throw new IllegalArgumentException("Plot name is required.");
        }
        if (polygonCoordinates == null) {
            throw new IllegalArgumentException("Polygon coordinates are required.");
        }
        if (areaSize == null) {
            throw new IllegalArgumentException("Area size is required.");
        }
    }

}
