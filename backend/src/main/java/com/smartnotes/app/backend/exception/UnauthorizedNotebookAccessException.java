package com.smartnotes.app.backend.exception;

public class UnauthorizedNotebookAccessException extends RuntimeException {
    public UnauthorizedNotebookAccessException(String message) {
        super(message);
    }
}