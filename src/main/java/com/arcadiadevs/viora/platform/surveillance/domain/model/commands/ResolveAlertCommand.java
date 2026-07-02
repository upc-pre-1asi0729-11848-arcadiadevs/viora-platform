package com.arcadiadevs.viora.platform.surveillance.domain.model.commands;

/**
 * Command to resolve an alert (the threat was addressed — by the producer directly
 * or through a closed intervention). Ends the alert lifecycle in RESOLVED.
 */
public record ResolveAlertCommand(Long alertId) {
}
