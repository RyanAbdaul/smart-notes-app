package com.smartnotes.app.backend.exception;

import java.util.UUID;

public class UnauthorizedNotebookAccessException extends RuntimeException {
    public UnauthorizedNotebookAccessException(String message) {
        super(message);
    }

    public UnauthorizedNotebookAccessException(UUID notebookId, UUID userId) {
        super("Unauthorized access to notebook with id: " + notebookId + " for user with id: " + userId);
    }
}