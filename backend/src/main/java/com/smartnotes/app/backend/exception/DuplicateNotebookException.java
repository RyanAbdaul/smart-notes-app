package com.smartnotes.app.backend.exception;

import java.util.UUID;

public class DuplicateNotebookException extends RuntimeException {
    public DuplicateNotebookException(String message) {
        super(message);
    }

    public DuplicateNotebookException(String notebookName, UUID userId) {
        super("Notebook with name '" + notebookName + "' already exists for user with id: " + userId);
    }
}