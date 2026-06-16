package com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources;

public record PlotSummaryResource(
    String name,
    String location,
    Double hectares
) {}
