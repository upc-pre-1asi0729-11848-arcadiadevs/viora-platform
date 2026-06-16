package com.arcadiadevs.viora.platform.surveillance.interfaces.rest;

import com.arcadiadevs.viora.platform.surveillance.application.queryservices.SymptomQueryService;
import com.arcadiadevs.viora.platform.surveillance.domain.model.queries.GetAllSymptomsQuery;
import com.arcadiadevs.viora.platform.surveillance.interfaces.rest.transform.SymptomResourceFromAggregateAssembler;
import io.swagger.v3.oas.annotations.Operation;
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

@RestController
@RequestMapping(value = "/api/v1/symptom-dictionary-items", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Symptom Dictionary Items", description = "Symptoms Catalog Management")
public class SymptomDictionaryItemsController {

    private final SymptomQueryService symptomQueryService;

    @GetMapping
    @Operation(
            summary = "Get available symptoms catalog",
            description = "Returns the catalog of available symptoms from the database that can be selected in a pest sighting report."
    )
    public ResponseEntity<?> getSymptoms(
            @RequestHeader(name = HttpHeaders.ACCEPT_LANGUAGE, defaultValue = "en") String language
    ) {
        var query = new GetAllSymptomsQuery();
        var symptoms = symptomQueryService.handle(query);

        var resources = symptoms.stream()
                .map(aggregate -> SymptomResourceFromAggregateAssembler.toResourceFromAggregate(aggregate, language))
                .collect(Collectors.toList());

        return ResponseEntity.ok(resources);
    }
}
