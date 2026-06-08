package com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AreaSize;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotName;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PolygonCoordinates;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;

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
    /**
     * The unique identifier for the plot.
     */
    @Setter
    private Long id;

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
     * Indicates whether the plot is active.
     */
    private Boolean active;

    /**
     * Default constructor for the Plot.
     */
    public Plot() {
        this.cropType = Strings.EMPTY;
    }


    /**
     * Constructor for Plot
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
        this.userId = userId;
        this.name = name;
        this.polygonCoordinates = polygonCoordinates;
        this.areaSize = areaSize;
        this.cropType = sanitizeText(cropType);
        this.variety = sanitizeText(variety);
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
        this.name = name;
        this.areaSize = areaSize;
        this.cropType = sanitizeText(cropType);
        this.variety = sanitizeText(variety);
        return this;
    }

    /**
     * Deactivates the plot.
     *
     * <p>
     * This method is useful when the system needs logical deletion
     * instead of physical deletion.
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
     * Sanitizes optional text fields.
     * @param value The text value.
     * @return The sanitized value.
     */
    private String sanitizeText(String value) {
        return value == null ? Strings.EMPTY : value.trim();
    }

}
