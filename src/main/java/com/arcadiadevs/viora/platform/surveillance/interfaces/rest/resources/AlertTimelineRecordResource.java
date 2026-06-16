package com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources;

import java.util.Date;

public record AlertTimelineRecordResource(
        String tag,
        String title,
        String description,
        Date createdAt
) {
}
