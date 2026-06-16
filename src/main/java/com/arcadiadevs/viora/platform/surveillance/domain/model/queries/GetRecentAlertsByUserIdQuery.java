package com.arcadiadevs.viora.platform.surveillance.domain.model.queries;

public record GetRecentAlertsByUserIdQuery(Long userId, int limit) {
    public GetRecentAlertsByUserIdQuery {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID is required and must be positive.");
        }
        if (limit <= 0) {
            limit = 3; // default limit
        }
    }
}
