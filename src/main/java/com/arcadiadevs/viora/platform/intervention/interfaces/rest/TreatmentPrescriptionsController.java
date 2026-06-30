package com.arcadiadevs.viora.platform.intervention.interfaces.rest;

import com.arcadiadevs.viora.platform.intervention.application.commandservices.TreatmentPrescriptionCommandService;
import com.arcadiadevs.viora.platform.intervention.application.queryservices.TreatmentPrescriptionQueryService;
import com.arcadiadevs.viora.platform.intervention.domain.model.queries.GetTreatmentPrescriptionByIdQuery;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.CreateTreatmentPrescriptionResource;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.LogFieldInspectionDataResource;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.PrescribeTreatmentResource;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.TreatmentPrescriptionResource;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.transform.TreatmentPrescriptionResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/treatment-prescriptions")
@Tag(name = "Treatment Prescriptions", description = "Treatment Prescription Management Endpoints")
public class TreatmentPrescriptionsController {

    private final TreatmentPrescriptionCommandService commandService;
    private final TreatmentPrescriptionQueryService queryService;

    public TreatmentPrescriptionsController(TreatmentPrescriptionCommandService commandService, TreatmentPrescriptionQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping
    @Operation(summary = "Initialize a new treatment prescription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Treatment prescription created"),
            @ApiResponse(responseCode = "400", description = "Invalid request or prescription already exists")
    })
    public ResponseEntity<TreatmentPrescriptionResource> createTreatmentPrescription(@RequestBody CreateTreatmentPrescriptionResource resource) {
        var command = TreatmentPrescriptionResourceAssembler.toCommandFromResource(resource);
        var treatmentPrescription = commandService.handle(command);
        if (treatmentPrescription.isEmpty()) return ResponseEntity.badRequest().build();
        var treatmentPrescriptionResource = TreatmentPrescriptionResourceAssembler.toResourceFromDomain(treatmentPrescription.get());
        return new ResponseEntity<>(treatmentPrescriptionResource, HttpStatus.CREATED);
    }

    @GetMapping("/{treatmentPrescriptionId}")
    @Operation(summary = "Get a treatment prescription by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Treatment prescription found"),
            @ApiResponse(responseCode = "404", description = "Treatment prescription not found")
    })
    public ResponseEntity<TreatmentPrescriptionResource> getTreatmentPrescriptionById(@PathVariable Long treatmentPrescriptionId) {
        var query = new GetTreatmentPrescriptionByIdQuery(treatmentPrescriptionId);
        var treatmentPrescription = queryService.handle(query);
        if (treatmentPrescription.isEmpty()) return ResponseEntity.notFound().build();
        var treatmentPrescriptionResource = TreatmentPrescriptionResourceAssembler.toResourceFromDomain(treatmentPrescription.get());
        return ResponseEntity.ok(treatmentPrescriptionResource);
    }

    @PostMapping("/{treatmentPrescriptionId}/field-inspections")
    @Operation(summary = "Log field inspection data for a prescription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Field inspection logged successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or invalid prescription status")
    })
    public ResponseEntity<TreatmentPrescriptionResource> logFieldInspection(@PathVariable Long treatmentPrescriptionId, @RequestBody LogFieldInspectionDataResource resource) {
        var command = TreatmentPrescriptionResourceAssembler.toCommandFromResource(treatmentPrescriptionId, resource);
        var treatmentPrescription = commandService.handle(command);
        if (treatmentPrescription.isEmpty()) return ResponseEntity.badRequest().build();
        var treatmentPrescriptionResource = TreatmentPrescriptionResourceAssembler.toResourceFromDomain(treatmentPrescription.get());
        return ResponseEntity.ok(treatmentPrescriptionResource);
    }

    @PostMapping("/{treatmentPrescriptionId}/agrochemical-prescriptions")
    @Operation(summary = "Issue an agrochemical prescription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Treatment prescribed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or missing prior inspection")
    })
    public ResponseEntity<TreatmentPrescriptionResource> prescribeTreatment(@PathVariable Long treatmentPrescriptionId, @RequestBody PrescribeTreatmentResource resource) {
        var command = TreatmentPrescriptionResourceAssembler.toCommandFromResource(treatmentPrescriptionId, resource);
        var treatmentPrescription = commandService.handle(command);
        if (treatmentPrescription.isEmpty()) return ResponseEntity.badRequest().build();
        var treatmentPrescriptionResource = TreatmentPrescriptionResourceAssembler.toResourceFromDomain(treatmentPrescription.get());
        return ResponseEntity.ok(treatmentPrescriptionResource);
    }
}
