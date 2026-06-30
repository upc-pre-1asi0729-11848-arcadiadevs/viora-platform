package com.arcadiadevs.viora.platform.intervention.interfaces.rest;

import com.arcadiadevs.viora.platform.intervention.domain.model.queries.GetSpecialistCandidatesByAlertIdQuery;
import com.arcadiadevs.viora.platform.intervention.application.queryservices.SpecialistCandidatesQueryService;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.SpecialistCandidateResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for specialist candidates.
 */
@RestController
@RequestMapping(value = "/api/v1/specialist-candidates")
@Tag(name = "Specialist Candidates", description = "Endpoints for finding specialist candidates")
public class SpecialistCandidatesController {

    private final SpecialistCandidatesQueryService specialistCandidatesQueryService;

    public SpecialistCandidatesController(SpecialistCandidatesQueryService specialistCandidatesQueryService) {
        this.specialistCandidatesQueryService = specialistCandidatesQueryService;
    }

    /**
     * Gets specialist candidates based on an alert.
     *
     * @param alertId the ID of the alert
     * @param limit   the maximum number of candidates to return
     * @return a list of specialist candidate resources
     */
    @GetMapping
    @Operation(summary = "Get specialist candidates for an alert")
    public ResponseEntity<List<SpecialistCandidateResource>> getSpecialistCandidates(
            @RequestParam Long alertId,
            @RequestParam(defaultValue = "3") Integer limit) {
        var query = new GetSpecialistCandidatesByAlertIdQuery(alertId, limit);
        var candidates = specialistCandidatesQueryService.handle(query);
        return ResponseEntity.ok(candidates);
    }
}
