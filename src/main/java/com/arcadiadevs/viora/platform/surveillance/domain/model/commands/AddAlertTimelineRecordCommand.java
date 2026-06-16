package com.arcadiadevs.viora.platform.surveillance.domain.model.commands;

public record AddAlertTimelineRecordCommand(
        Long alertId,
        String tag,
        String title,
        String description
) {
    public AddAlertTimelineRecordCommand {
        if (alertId == null || alertId <= 0) {
            throw new IllegalArgumentException("Alert ID must be a positive number.");
        }
        if (tag == null || tag.isBlank()) {
            throw new IllegalArgumentException("Tag cannot be null or empty.");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or empty.");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or empty.");
        }
    }
}
