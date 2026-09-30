package com.smartnotes.app.backend.exception;

import java.util.UUID;

public class TooMuchAttemptsException extends RuntimeException {
    public TooMuchAttemptsException(String message) {
        super(message);
    }

    public TooMuchAttemptsException(String notebookName, UUID userId) {
        super("Notebook with name '" + notebookName + "' already exists for user with id: " + userId);
    }
}