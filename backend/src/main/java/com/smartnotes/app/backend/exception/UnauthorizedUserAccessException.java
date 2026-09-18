package com.smartnotes.app.backend.exception;

import java.util.UUID;

public class UnauthorizedUserAccessException extends RuntimeException {
    public UnauthorizedUserAccessException(String message) {
        super(message);
    }

    public UnauthorizedUserAccessException(UUID userId, UUID currentUserId) {
        super("Unauthorized access to user with id: " + userId + " for current user with id: " + currentUserId);
    }
}