package com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources;

public record AlertSummaryResource(
    Long id,
    String type,
    String description,
    String severity,
    String date,
    String status,
    PlotSummaryResource plot
) {}
