package com.arcadiadevs.viora.platform.surveillance.domain.model.commands;

/**
 * Command to dismiss an alert directly by its id (e.g. the producer ruled it out
 * as a false alarm). Ends the alert lifecycle in DISMISSED.
 */
public record DismissAlertCommand(Long alertId, String reason) {
}
