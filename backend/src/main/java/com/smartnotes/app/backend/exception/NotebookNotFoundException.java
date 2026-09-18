package com.smartnotes.app.backend.exception;

import java.util.UUID;

public class NotebookNotFoundException extends RuntimeException {
    public NotebookNotFoundException(String message) {
        super(message);
    }

    public NotebookNotFoundException(UUID notebookId) {
        super("Notebook not found with id: " + notebookId);
    }
}