package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.converters;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NutritionInputRecommendation;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NutritionInputStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Collections;
import java.util.List;

/**
 * Converts lists of NutritionInputRecommendation values to and from their
 * JSON persistence representation.
 */
@Converter
public class NutritionInputRecommendationAttributeConverter
        implements AttributeConverter<List<NutritionInputRecommendation>, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Flat JSON document for a nutrition input recommendation.
     */
    record NutritionInputRecommendationDocument(
            String value,
            String purpose,
            Double dosage,
            String dosageUnit,
            String status
    ) {
    }

    /**
     * Converts a list of nutrition input recommendations into a JSON array.
     *
     * @param attribute The nutrition input recommendations.
     * @return The database representation.
     */
    @Override
    public String convertToDatabaseColumn(List<NutritionInputRecommendation> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }

        var documents = attribute.stream()
                .map(input -> new NutritionInputRecommendationDocument(
                        input.getValue(),
                        input.getPurpose(),
                        input.getDosage(),
                        input.getDosageUnit(),
                        input.getStatus().name()
                ))
                .toList();

        try {
            return OBJECT_MAPPER.writeValueAsString(documents);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Unable to serialize nutrition input recommendations.", exception);
        }
    }

    /**
     * Reconstructs a list of nutrition input recommendations from a JSON array.
     *
     * @param dbData The database representation.
     * @return The nutrition input recommendations.
     */
    @Override
    public List<NutritionInputRecommendation> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return Collections.emptyList();
        }

        try {
            var documents = OBJECT_MAPPER.readValue(
                    dbData,
                    new TypeReference<List<NutritionInputRecommendationDocument>>() {
                    }
            );

            return documents.stream()
                    .map(document -> new NutritionInputRecommendation(
                            document.value(),
                            document.purpose(),
                            document.dosage(),
                            document.dosageUnit(),
                            NutritionInputStatus.fromString(document.status())
                    ))
                    .toList();
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Invalid nutrition input recommendations database format.", exception);
        }
    }
}
