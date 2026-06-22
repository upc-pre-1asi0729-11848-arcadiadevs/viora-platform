package com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects;

/**
 * Triage outcome of a pest sighting report.
 *
 * <ul>
 *   <li>{@code LOGGED} — weak signal; recorded for community epidemiology, no alert raised.</li>
 *   <li>{@code NEEDS_INSPECTION} — a real signal exists but is not yet corroborated; a light
 *       inspection alert is raised so the grower goes and checks.</li>
 *   <li>{@code CONFIRMED} — subjective and objective signals agree (or a quarantine pathogen is
 *       suspected), or a grower confirmed the threat after a field inspection; a high-priority
 *       alert is raised.</li>
 *   <li>{@code RULED_OUT} — a grower inspected the plot and verified there was no real threat
 *       (verified false positive); still recorded for community epidemiology.</li>
 *   <li>{@code UNDER_REVIEW} — legacy state from the pre-triage binary model; kept so existing
 *       persisted reports still deserialize. Clients treat it like {@code NEEDS_INSPECTION}.</li>
 * </ul>
 */
public enum ReportStatus {
    LOGGED,
    NEEDS_INSPECTION,
    CONFIRMED,
    RULED_OUT,
    UNDER_REVIEW
}
