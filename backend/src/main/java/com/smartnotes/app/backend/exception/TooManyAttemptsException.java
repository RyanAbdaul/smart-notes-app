package com.smartnotes.app.backend.exception;

import java.util.UUID;

public class TooManyAttemptsException extends RuntimeException {
    public TooManyAttemptsException(String message) {
        super(message);
    }

    public TooManyAttemptsException(String notebookName, UUID userId) {
        super("Notebook with name '" + notebookName + "' already exists for user with id: " + userId);
    }
}