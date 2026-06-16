package com.arcadiadevs.viora.platform.surveillance.domain.exceptions;

public class AlertNotFoundException extends RuntimeException {
    public AlertNotFoundException(Long alertId) {
        super("Alert with ID " + alertId + " was not found.");
    }
}
