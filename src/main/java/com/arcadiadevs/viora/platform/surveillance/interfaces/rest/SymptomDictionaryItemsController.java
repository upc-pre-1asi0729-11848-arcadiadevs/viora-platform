package com.arcadiadevs.viora.platform.surveillance.interfaces.rest;

import com.arcadiadevs.viora.platform.surveillance.application.queryservices.SymptomQueryService;
import com.arcadiadevs.viora.platform.surveillance.domain.model.queries.GetAllSymptomsQuery;
import com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources.SymptomResource;
import com.arcadiadevs.viora.platform.surveillance.interfaces.rest.transform.SymptomResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

/**
 * REST controller that exposes the Symptoms Catalog endpoints.
 */
@RestController
@RequestMapping(value = "/api/v1/symptom-dictionary-items", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Symptom Dictionary Items", description = "Symptoms Catalog Management")
public class SymptomDictionaryItemsController {

    private final SymptomQueryService symptomQueryService;

    /**
     * Returns the catalog of available symptoms from the database that can be selected in a pest sighting report.
     *
     * @param language the desired language for the symptom descriptions (e.g., 'en', 'es')
     * @return 200 OK with list of SymptomResource
     */
    @GetMapping
    @Operation(
            summary = "Get available symptoms catalog",
            description = "Returns the catalog of available symptoms from the database that can be selected in a pest sighting report."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Symptoms retrieved successfully",
                    content = @Content(schema = @Schema(implementation = SymptomResource.class)))
    })
    public ResponseEntity<?> getSymptoms(
            @RequestHeader(name = HttpHeaders.ACCEPT_LANGUAGE, defaultValue = "en") String language
    ) {
        var query = new GetAllSymptomsQuery();
        var symptoms = symptomQueryService.handle(query);

        var resources = symptoms.stream()
                .map(entity -> SymptomResourceFromEntityAssembler.toResourceFromEntity(entity, language))
                .collect(Collectors.toList());

        return ResponseEntity.ok(resources);
    }
}
