package com.arcadiadevs.viora.platform.surveillance.domain.exceptions;

public class AlertAlreadyReviewedException extends RuntimeException {
    public AlertAlreadyReviewedException(Long alertId) {
        super("Alert with ID " + alertId + " has already been reviewed.");
    }
}
