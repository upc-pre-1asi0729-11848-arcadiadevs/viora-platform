package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.converters;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NutritionPlanStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts NutritionPlanStatus values to and from their persistence representation.
 */
@Converter
public class NutritionPlanStatusAttributeConverter implements AttributeConverter<NutritionPlanStatus, String> {

    /**
     * Converts a nutrition plan status into its name.
     *
     * @param attribute The nutrition plan status.
     * @return The database representation.
     */
    @Override
    public String convertToDatabaseColumn(NutritionPlanStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    /**
     * Reconstructs a nutrition plan status from its name.
     *
     * @param dbData The database representation.
     * @return The nutrition plan status.
     */
    @Override
    public NutritionPlanStatus convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        return NutritionPlanStatus.fromString(dbData);
    }
}
