package com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources;

public record AlertTimelineRecordResource(
        String tag,
        String title,
        String description,
        java.time.LocalDateTime createdAt
) {
}
