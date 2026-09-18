package com.smartnotes.app.backend.exception;

import java.util.UUID;

public class UnauthorizedTagAccessException extends RuntimeException {
    public UnauthorizedTagAccessException(String message) {
        super(message);
    }

    public UnauthorizedTagAccessException(UUID tagId, UUID userId) {
        super("Unauthorized access to tag with id: " + tagId + " for user with id: " + userId);
    }
}