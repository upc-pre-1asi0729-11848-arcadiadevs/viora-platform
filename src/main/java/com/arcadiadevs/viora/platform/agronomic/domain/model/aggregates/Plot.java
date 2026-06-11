package com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AreaSize;
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
     * Updates the general information of the plot.
     * @param name The new plot name.
     * @param areaSize The new area size.
     * @param cropType The new crop type.
     * @param variety The new crop variety.
     * @return The update plot.
     */
    public Plot updateInformation(
            PlotName name,
            AreaSize areaSize,
            String cropType,
            String variety
    ) {
        return updateInformation(name, areaSize, cropType, variety, this.location, this.campaign, this.notes);
    }

    /**
     * Updates the general information of the plot, including descriptive metadata.
     * @param name The new plot name.
     * @param areaSize The new area size.
     * @param cropType The new crop type.
     * @param variety The new crop variety.
     * @param location The new plot location.
     * @param campaign The new production campaign.
     * @param notes The new grower notes.
     * @return The updated plot.
     */
    public Plot updateInformation(
            PlotName name,
            AreaSize areaSize,
            String cropType,
            String variety,
            String location,
            String campaign,
            String notes
    ) {
        if (name == null) {
            throw new IllegalArgumentException("Plot name is required.");
        }
        if (areaSize == null) {
            throw new IllegalArgumentException("Area size is required.");
        }

        this.name = name;
        this.areaSize = areaSize;
        this.cropType = sanitizeText(cropType, CROP_TYPE_MAX_LENGTH, "Crop type");
        this.variety = sanitizeText(variety, VARIETY_MAX_LENGTH, "Variety");
        this.location = sanitizeText(location, LOCATION_MAX_LENGTH, "Location");
        this.campaign = sanitizeText(campaign, CAMPAIGN_MAX_LENGTH, "Campaign");
        this.notes = sanitizeText(notes, NOTES_MAX_LENGTH, "Notes");
        return this;
    }

    /**
     * Updates the geographic boundary and its associated productive area.
     *
     * @param polygonCoordinates The new polygon coordinates.
     * @param areaSize The new area size.
     * @return The updated plot.
     */
    public Plot updateBoundary(PolygonCoordinates polygonCoordinates, AreaSize areaSize) {
        if (polygonCoordinates == null) {
            throw new IllegalArgumentException("Polygon coordinates are required.");
        }
        if (areaSize == null) {
            throw new IllegalArgumentException("Area size is required.");
        }

        this.polygonCoordinates = polygonCoordinates;
        this.areaSize = areaSize;
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
